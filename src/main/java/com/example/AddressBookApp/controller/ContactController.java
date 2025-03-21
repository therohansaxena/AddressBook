package com.example.AddressBookApp.controller;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.repository.ContactRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contact API", description = "Operations related to Contact Management")
public class ContactController {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // GET All Contacts
    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        return ResponseEntity.ok(contacts);
    }

    // GET Contact by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getContactById(@PathVariable Long id) {
        try {
            Optional<Contact> contact = contactRepository.findById(id);
            if (contact.isPresent()) {
                return ResponseEntity.ok(contact.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Contact with ID " + id + " not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong. Please try again."));
        }
    }

    // POST - Create Contact
    @PostMapping("/add")
    public ResponseEntity<Object> createContact(@RequestBody ContactDTO contactDTO) {
        try {
            // Manual validation
            List<String> errors = validateContact(contactDTO);

            // If errors exist, return them
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Save the contact
            Contact contact = new Contact();
            contact.setName(contactDTO.getName());
            contact.setPhoneNumber(contactDTO.getPhoneNumber());
            contact.setEmail(contactDTO.getEmail());
            contact.setAddress(contactDTO.getAddress());

            Contact savedContact = contactRepository.save(contact);
            return ResponseEntity.ok(savedContact);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong. Please try again."));
        }
    }

    // PUT - Update Contact by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateContact(@PathVariable Long id, @RequestBody ContactDTO contactDTO) {
        try {
            // Check if contact exists
            Optional<Contact> contactOptional = contactRepository.findById(id);
            if (contactOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Contact with ID " + id + " not found"));
            }

            // Manual validation
            List<String> errors = validateContact(contactDTO);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Update contact
            Contact contact = contactOptional.get();
            contact.setName(contactDTO.getName());
            contact.setPhoneNumber(contactDTO.getPhoneNumber());
            contact.setEmail(contactDTO.getEmail());
            contact.setAddress(contactDTO.getAddress());

            Contact updatedContact = contactRepository.save(contact);
            return ResponseEntity.ok(updatedContact);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong. Please try again."));
        }
    }

    // DELETE - Remove Contact by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteContact(@PathVariable Long id) {
        try {
            if (!contactRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Contact with ID " + id + " not found"));
            }
            contactRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Contact deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong. Please try again."));
        }
    }

    // Validation Method
    private List<String> validateContact(ContactDTO contactDTO) {
        List<String> errors = new ArrayList<>();

        if (contactDTO.getName() == null || contactDTO.getName().trim().isEmpty()) {
            errors.add("Name is required");
        } else if (!contactDTO.getName().matches("^[A-Z][a-zA-Z\\s]*$")) {
            errors.add("Name must start with a capital letter and contain only letters and spaces");
        }

        if (contactDTO.getPhoneNumber() == null || !contactDTO.getPhoneNumber().matches("^[6-9]\\d{9}$")) {
            errors.add("Phone number must be a valid 10-digit number starting with 6-9");
        }

        if (contactDTO.getEmail() == null || !contactDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.add("Invalid email format");
        }

        // ✅ Add validation for address
        if (contactDTO.getAddress() == null || contactDTO.getAddress().trim().isEmpty()) {
            errors.add("Address cannot be null or empty");
        }

        return errors;
    }

}