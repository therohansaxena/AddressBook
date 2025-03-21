package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContactService implements IContactService {

    @Autowired
    private ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // ✅ Convert Model to DTO
    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhoneNumber(), contact.getEmail(), contact.getAddress());
    }

    // ✅ Convert DTO to Model
    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(contactDTO.getId(), contactDTO.getName(), contactDTO.getPhoneNumber(), contactDTO.getEmail(), contactDTO.getAddress());
    }

    // ✅ Fetch all contacts (Cache result)
    @Override
    @Cacheable(value = "allContacts")
    public List<ContactDTO> getAllContacts() {
        log.info("Fetching all contacts from the database.");
        return contactRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Fetch a single contact by ID (Cache result)
    @Override
    @Cacheable(value = "contacts", key = "#id")
    public ContactDTO getContactById(Long id) {
        log.info("Fetching contact with ID: {}", id);
        Optional<Contact> contact = contactRepository.findById(id);
        if (contact.isPresent()) {
            log.info("Contact found: {}", contact.get());
        } else {
            log.warn("Contact with ID {} not found.", id);
        }
        return contact.map(this::convertToDTO).orElse(null);
    }

    // ✅ Create new contact (Evict cache)
    @Override
    @CacheEvict(value = {"contacts", "allContacts"}, allEntries = true)
    public ContactDTO createContact(ContactDTO contactDTO) {
        log.info("Creating new contact: {}", contactDTO);
        Contact contact = convertToEntity(contactDTO);
        Contact savedContact = contactRepository.save(contact);
        log.info("Contact saved successfully with ID: {}", savedContact.getId());
        return convertToDTO(savedContact);
    }

    // ✅ Update contact (Evict cache)
    @Override
    @CacheEvict(value = {"contacts", "allContacts"}, allEntries = true)
    public ContactDTO updateContact(Long id, ContactDTO contactDTO) {
        Optional<Contact> optionalContact = contactRepository.findById(id);

        if (optionalContact.isPresent()) {
            Contact contact = optionalContact.get();
            contact.setName(contactDTO.getName());
            contact.setPhoneNumber(contactDTO.getPhoneNumber());
            contact.setEmail(contactDTO.getEmail());
            contactRepository.save(contact);
            log.info("Contact updated successfully: {}", contact);
            return convertToDTO(contact);
        } else {
            log.warn("Attempted to update non-existing contact with ID: {}", id);
        }
        return null;
    }

    // ✅ Delete contact (Evict cache)
    @Override
    @CacheEvict(value = {"contacts", "allContacts"}, key = "#id")
    public void deleteContact(Long id) {
        log.info("Deleting contact with ID: {}", id);
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
            log.info("Contact with ID {} deleted successfully.", id);
        } else {
            log.warn("Attempted to delete non-existing contact with ID: {}", id);
        }
    }
}