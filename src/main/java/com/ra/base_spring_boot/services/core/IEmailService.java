package com.ra.base_spring_boot.services.core;

public interface IEmailService {
    void sendEmail(String to, String subject, String text);
    void createAndSendOtp(String email);
}
