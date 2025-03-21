package com.example.AddressBookApp.controller;

import com.example.AddressBookApp.dto.LoginDTO;
import com.example.AddressBookApp.dto.UserDTO;
import com.example.AddressBookApp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        String response = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Return 201 Created
    }

    // Login User and Generate JWT Token
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginRequest) {
        String token = userService.authenticateUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (token.equals("User not found!") || token.equals("Invalid email or password!")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", token)); // 401 Unauthorized
        }

        return ResponseEntity.ok(Map.of("message", "Login successful!", "token", token));
    }
}