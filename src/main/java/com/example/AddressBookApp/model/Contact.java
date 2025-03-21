package com.example.AddressBookApp.model;
import jakarta.persistence.*;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Ensure ID is auto-generated
    private Long id;

    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    // Constructors
    public Contact() {}  // Default constructor needed for JPA

    public Contact(Long id, String name, String phoneNumber, String email, String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Getters & Setters
    public Long getId() { return id; }  // Ensure getter is present
    public void setId(Long id) { this.id = id; }  // This should not be manually set

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}