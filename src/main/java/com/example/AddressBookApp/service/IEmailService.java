package com.example.AddressBookApp.service;

public interface IEmailService {
    public void sendEmail(String toEmail, String subject, String body);
}