package com.example.AddressBookApp.controller;

import com.example.AddressBookApp.dto.LoginDTO;
import com.example.AddressBookApp.dto.UserDTO;
import com.example.AddressBookApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // Create a map to hold the user-friendly error messages
            Map<String, String> errors = new HashMap<>();

            // Loop through all validation errors and add user-friendly messages
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage(); // Use the message from the annotation (e.g., @NotBlank, @Email)
                errors.put(field, message);
            });

            // Return the errors with a BAD_REQUEST status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            // Call service method to register user
            String response = userService.registerUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", response));
        } catch (Exception e) {
            // Handle exception and return error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    // Login User and Generate JWT Token
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginRequest, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            // Call service method to authenticate user and generate JWT
            String token = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            if ("User not found!".equals(token) || "Invalid email or password!".equals(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", token));
            }
            return ResponseEntity.ok(Map.of("message", "Login successful!", "token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    // Forgot Password
    @PutMapping("/forgot-Password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email, @Valid @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password cannot be empty!"));
            }

            String response = userService.forgotPassword(email, newPassword);
            return ResponseEntity.ok(Map.of("message", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to reset password: " + e.getMessage()));
        }
    }

    // Reset Password
    @PutMapping("/reset-Password/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable String email, @Valid @RequestParam String currentPassword, @Valid @RequestParam String newPassword) {
        try {
            String response = userService.resetPassword(email, currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Password reset failed: " + e.getMessage()));
        }
    }
}