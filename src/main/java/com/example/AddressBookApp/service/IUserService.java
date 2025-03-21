package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.UserDTO;

public interface IUserService {
    public String registerUser(UserDTO userdto);
    public String authenticateUser(String email, String password);
}