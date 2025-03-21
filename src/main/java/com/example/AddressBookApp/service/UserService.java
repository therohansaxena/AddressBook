package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.UserDTO;
import com.example.AddressBookApp.model.User;
import com.example.AddressBookApp.repository.UserRepository;
import com.example.AddressBookApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register User
    public String registerUser(UserDTO userdto) {
        if (userRepository.existsByEmail(userdto.getEmail())) {
            return "Email is already in use!";
        }

        User user = new User();
        user.setUsername(userdto.getName());
        user.setEmail(userdto.getEmail());
        user.setPassword(passwordEncoder.encode(userdto.getPassword())); // Encrypt password
        String subject = "Welcome to Our Platform!";
        String body = "<h1>Hello " + userdto.getName() + "!</h1>"
                + "<p>Thank you for registering on our platform.</p>"
                + "<p>We are excited to have you on board.</p>";

        emailService.sendEmail(user.getEmail(), subject, body);

        userRepository.save(user);
        return "User registered successfully!";
    }

    // Authenticate User and Generate Token
    public String authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid email or password!";
        }

        // Generate JWT Token using HMAC256
        return jwtUtil.generateToken(email);
    }
}