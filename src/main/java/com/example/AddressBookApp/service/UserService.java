package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.UserDTO;
import com.example.AddressBookApp.model.User;
import com.example.AddressBookApp.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.example.AddressBookApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
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
    @Override
    public String registerUser(UserDTO userdto) {
        log.info("Registering new user: {}", userdto.getEmail());
        if (userRepository.existsByEmail(userdto.getEmail())) {
            return "Email is already in use!";
        }

        User user = new User();
        user.setUsername(userdto.getName());
        user.setEmail(userdto.getEmail());
        user.setPassword(passwordEncoder.encode(userdto.getPassword())); // Encrypt password
        userRepository.save(user);

        // Send welcome email
        String subject = "Welcome to Our Platform!";
        String body = "<h1>Hello " + userdto.getName() + "!</h1>"
                + "<p>Thank you for registering on our platform.</p>";
        emailService.sendEmail(user.getEmail(), subject, body);

        log.info("User {} registered successfully.", user.getEmail());

        // Clear Redis cache when a new user is registered
        clearUserCache(user.getEmail());

        return "User registered successfully!";
    }

    // Authenticate User and Generate Token
    @Override
    @Cacheable(value = "users", key = "#email")  // Cache user authentication
    public String authenticateUser(String email, String password) {
        log.info("Login attempt for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Login failed: No user found for email: {}", email);
            return "User not found!";
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed: Incorrect password for email: {}", email);
            return "Invalid email or password!";
        }

        log.info("Login successful for user: {}", email);
        return jwtUtil.generateToken(email);
    }

    // Forgot Password Implementation
    @Override
    @CacheEvict(value = "users", key = "#email")  // Remove cached user data when password is updated
    public String forgotPassword(String email, String newPassword) {
        log.info("Processing forgot password request for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Forgot password request failed: No user found for email: {}", email);
            return "Sorry! We cannot find the user email: " + email;
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send email notification
        String subject = "Password Change Notification";
        String content = "<h2>Hello " + user.getUsername() + ",</h2>"
                + "<p>Your password has been changed successfully.</p>";
        emailService.sendEmail(user.getEmail(), subject, content);

        log.info("Password updated successfully for email: {}", email);
        return "Password has been changed successfully!";
    }

    // Reset Password Implementation
    @Override
    @CacheEvict(value = "users", key = "#email")  // Clear cache after password reset
    public String resetPassword(String email, String currentPassword, String newPassword) {
        log.info("Resetting password for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Password reset failed: No user found for email: {}", email);
            return "User not found with email: " + email;
        }
        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Password reset failed: Incorrect current password for email: {}", email);
            return "Current password is incorrect!";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send email notification
        String subject = "Password Reset Notification";
        String content = "<h2>Hello " + user.getUsername() + ",</h2>"
                + "<p>Your password has been reset successfully.</p>";
        emailService.sendEmail(user.getEmail(), subject, content);

        log.info("Password reset successful for email: {}", email);
        return "Password reset successfully!";
    }

    // Clear user cache after an update
    private void clearUserCache(String email) {
        log.info("Clearing Redis cache for user: {}", email);
    }
}