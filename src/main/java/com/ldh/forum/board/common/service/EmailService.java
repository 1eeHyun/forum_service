package com.ldh.forum.board.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Random random = new Random();

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Generate code (6 digits)
    public String generateVerificationCode() {
        return String.valueOf(100000 + random.nextInt(900000)); // 100000 ~ 999999
    }

    // Send an email
    public boolean sendVerificationEmail(String toEmail, String verificationCode) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + verificationCode);
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Email sending failed: " + e.getMessage());
            return false;
        }

        return true;
    }
}
