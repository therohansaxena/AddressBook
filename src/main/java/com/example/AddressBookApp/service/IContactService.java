package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;

import java.util.List;

public interface IContactService {
    List<ContactDTO> getAllContacts();
    ContactDTO getContactById(Long id);
    ContactDTO createContact(ContactDTO contactDTO);
    ContactDTO updateContact(Long id, ContactDTO contactDTO);
    void deleteContact(Long id);
}