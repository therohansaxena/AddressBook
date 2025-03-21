//package com.example.AddressBookApp.dto;
//
//public class ContactDTO {
//    private Long id;
//    private String name;
//    private String phoneNumber;
//    private String email;
//    private String address;
//
//    // Constructor
//    public ContactDTO(Long id, String name, String phoneNumber, String email, String address) {
//        this.id = id;
//        this.name = name;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.address = address;
//    }
//
//    // Getters and setters
//    public Long getId() {
//        return id;
//    }
//    public String getName() {
//        return name;
//    }
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//    public String getEmail() {
//        return email;
//    }
//    public String getAddress() {
//        return address;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//}

package com.example.AddressBookApp.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Long id;
    @NotBlank(message = "Name is required")
    @Pattern(
            regexp = "^[A-Z][a-zA-Z\\s]*$",
            message = "Name must start with a capital letter and contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be a valid 10-digit number starting with 6-9"
    )
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

}