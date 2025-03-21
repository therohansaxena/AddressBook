package com.example.AddressBookApp.controller;

import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService service;

    @GetMapping
    public List<Contact> getAllContacts() {
        return service.getAllContacts();
    }
    @PostMapping("/add")
    public Contact addContact(@RequestBody Contact contact) {
        return service.saveContact(contact);
    }

}