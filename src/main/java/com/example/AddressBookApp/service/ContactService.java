package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService implements IContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // ✅ Convert Model to DTO
    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getId(),contact.getName(), contact.getPhoneNumber(), contact.getEmail(), contact.getAddress());
    }

    // ✅ Convert DTO to Model
    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(contactDTO.getId(), contactDTO.getName(), contactDTO.getPhoneNumber(), contactDTO.getEmail(), contactDTO.getAddress());
    }

    @Override
    public List<ContactDTO> getAllContacts() {
        return contactRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactDTO getContactById(Long id) {
        Optional<Contact> contact = contactRepository.findById(id);
        return contact.map(this::convertToDTO).orElse(null);
    }

    @Override
    public ContactDTO createContact(ContactDTO contactDTO) {
        Contact contact = convertToEntity(contactDTO);
        Contact savedContact = contactRepository.save(contact);
        return convertToDTO(savedContact);
    }

    @Override
    public ContactDTO updateContact(Long id, ContactDTO contactDTO) {
        Optional<Contact> optionalContact = contactRepository.findById(id);

        if (optionalContact.isPresent()) {
            Contact contact = optionalContact.get();
            contact.setName(contactDTO.getName());
            contact.setPhoneNumber(contactDTO.getPhoneNumber());
            contact.setEmail(contactDTO.getEmail());
            contactRepository.save(contact);
            return convertToDTO(contact);
        }
        return null;
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}