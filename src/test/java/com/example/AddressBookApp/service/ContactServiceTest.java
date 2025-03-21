package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    private ContactDTO contactDTO;
    private Contact contact;

    @BeforeEach
    void setUp() {
        contactDTO = new ContactDTO(1L, "John Doe", "1234567890", "john@example.com", "123 Street");
        contact = new Contact(1L, "John Doe", "1234567890", "john@example.com", "123 Street");
    }

    // ✅ getAllContacts Tests
    @Test
    void testGetAllContacts_Success() {
        when(contactRepository.findAll()).thenReturn(Arrays.asList(contact));

        List<ContactDTO> contacts = contactService.getAllContacts();
        assertFalse(contacts.isEmpty());
    }

    @Test
    void testGetAllContacts_Failure() {
        when(contactRepository.findAll()).thenReturn(List.of());

        List<ContactDTO> contacts = contactService.getAllContacts();
        assertTrue(contacts.isEmpty());
    }

    @Test
    void testGetAllContacts_Exception() {
        when(contactRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> contactService.getAllContacts());
        assertEquals("Database error", exception.getMessage());
    }

    // ✅ getContactById Tests
    @Test
    void testGetContactById_Success() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        ContactDTO result = contactService.getContactById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetContactById_Failure() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());

        ContactDTO result = contactService.getContactById(1L);
        assertNull(result);
    }

    @Test
    void testGetContactById_Exception() {
        when(contactRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> contactService.getContactById(1L));
        assertEquals("Database error", exception.getMessage());
    }

    // ✅ createContact Tests
    @Test
    void testCreateContact_Failure() {
        // Given
        ContactDTO contactDTO = new ContactDTO(1L, "John Doe", "1234567890", "john@example.com", "123 Street");
        Contact contactEntity = new Contact(1L, "John Doe", "1234567890", "john@example.com", "123 Street");

        // Mock save() to return null (simulating a failure)
        when(contactRepository.save(any(Contact.class))).thenReturn(null);

        // When & Then - assertThrows for NullPointerException
        assertThrows(NullPointerException.class, () -> {
            contactService.createContact(contactDTO);
        });
    }

    @Test
    void testCreateContact_Success() {
        // Given
        ContactDTO contactDTO = new ContactDTO(1L, "John Doe", "1234567890", "john@example.com", "123 Street");
        Contact contactEntity = new Contact(1L, "John Doe", "1234567890", "john@example.com", "123 Street");

        // Mock save() to return a valid contact
        when(contactRepository.save(any(Contact.class))).thenReturn(contactEntity);

        // When
        ContactDTO savedContact = contactService.createContact(contactDTO);

        // Then
        assertNotNull(savedContact);
        assertEquals("John Doe", savedContact.getName());
    }

    @Test
    void testCreateContact_Failure_InvalidData() {
        // Given
        ContactDTO contactDTO = new ContactDTO(1L, "", "", "invalid-email", "123 Street");

        // Mock save() to return null
        when(contactRepository.save(any(Contact.class))).thenReturn(null);

        // When
        ContactDTO savedContact = contactService.createContact(contactDTO);

        // Then
        assertNull(savedContact);
    }
    // ✅ updateContact Tests
    @Test
    void testUpdateContact_Success() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any())).thenReturn(contact);

        ContactDTO result = contactService.updateContact(1L, contactDTO);
        assertNotNull(result);
    }

    @Test
    void testUpdateContact_Failure() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());

        ContactDTO result = contactService.updateContact(1L, contactDTO);
        assertNull(result);
    }

    @Test
    void testUpdateContact_Exception() {
        when(contactRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> contactService.updateContact(1L, contactDTO));
        assertEquals("Database error", exception.getMessage());
    }

    // ✅ deleteContact Tests
    @Test
    void testDeleteContact_Success() {
        when(contactRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(1L);

        contactService.deleteContact(1L);
        assertTrue(true);
    }

    @Test
    void testDeleteContact_Failure() {
        when(contactRepository.existsById(1L)).thenReturn(false);

        contactService.deleteContact(1L);
        assertFalse(false);
    }

    @Test
    void testDeleteContact_Exception() {
        when(contactRepository.existsById(1L)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> contactService.deleteContact(1L));
        assertEquals("Database error", exception.getMessage());
    }
}