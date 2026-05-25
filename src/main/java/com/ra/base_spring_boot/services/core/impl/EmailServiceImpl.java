package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.model.OTP;
import com.ra.base_spring_boot.repository.OtpRepository;
import com.ra.base_spring_boot.services.core.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;
    private final OtpRepository otpRepository;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }



    public void createAndSendOtp(String email) {
        String otp = generateOtp();
        otpRepository.save(new OTP(email, otp, LocalDateTime.now().plusMinutes(5), false));

        // Gửi email
        String subject = "Your OTP Code";
        String body = "Your OTP code is: " + otp + "\nIt will expire in 5 minutes.";

        sendEmail(email, subject, body);
        System.out.println("OTP for " + email + ": " + otp); // chỉ dev
    }

    private String generateOtp() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

}
