package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.UserDTO;
import com.example.AddressBookApp.model.User;
import com.example.AddressBookApp.repository.UserRepository;
import com.example.AddressBookApp.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("TestUser", "test@example.com", "password123");
        user = new User();
        user.setUsername(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }

    // 1. Test for registerUser()
    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.registerUser(userDTO);
        assertEquals("User registered successfully!", result);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        String result = userService.registerUser(userDTO);
        assertEquals("Email is already in use!", result);
    }

    @Test
    void testRegisterUser_Exception() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userService.registerUser(userDTO));
    }

    // 2. Test for authenticateUser()
    @Test
    void testAuthenticateUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("mock-token");

        String token = userService.authenticateUser(user.getEmail(), "password123");
        assertEquals("mock-token", token);
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        String result = userService.authenticateUser(user.getEmail(), "password123");
        assertEquals("User not found!", result);
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = userService.authenticateUser(user.getEmail(), "wrongPassword");
        assertEquals("Invalid email or password!", result);
    }

    // 3. Test for forgotPassword()
    @Test
    void testForgotPassword_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.forgotPassword(user.getEmail(), "newPassword123");
        assertEquals("Password has been changed successfully!", result);
    }

    @Test
    void testForgotPassword_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        String result = userService.forgotPassword(user.getEmail(), "newPassword123");
        assertEquals("Sorry! We cannot find the user email: " + user.getEmail(), result);
    }

    @Test
    void testForgotPassword_Exception() {
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userService.forgotPassword(user.getEmail(), "newPassword123"));
    }

    // 4. Test for resetPassword()
    @Test
    void testResetPassword_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.resetPassword(user.getEmail(), "password123", "newPassword123");
        assertEquals("Password reset successfully!", result);
    }

    @Test
    void testResetPassword_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        String result = userService.resetPassword(user.getEmail(), "password123", "newPassword123");
        assertEquals("User not found with email: " + user.getEmail(), result);
    }

    @Test
    void testResetPassword_InvalidCurrentPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = userService.resetPassword(user.getEmail(), "wrongPassword", "newPassword123");
        assertEquals("Current password is incorrect!", result);
    }
}