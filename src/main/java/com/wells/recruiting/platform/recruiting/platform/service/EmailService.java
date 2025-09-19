package com.wells.recruiting.platform.recruiting.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String recoveryLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contato@wellsjhones.com.br"); // Explicitly set sender
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click the following link to reset your password:\n" + recoveryLink
                + "\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);
    }
}
