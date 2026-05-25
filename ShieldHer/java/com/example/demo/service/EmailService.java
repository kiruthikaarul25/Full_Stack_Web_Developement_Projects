package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // DMCA removal request email
    public void sendRemovalRequest(String toEmail, 
                                   String photoUrl, 
                                   String foundUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("DMCA Removal Request — Unauthorized Photo");
        message.setText(
            "Dear Website Owner,\n\n" +
            "I am writing to request the immediate removal of my photo " +
            "that has been posted without my consent.\n\n" +
            "Photo URL: " + photoUrl + "\n" +
            "Found at: " + foundUrl + "\n\n" +
            "This is a violation of my privacy under IT Act 2000 " +
            "Section 66E.\n\n" +
            "Please remove this content within 48 hours.\n\n" +
            "Regards,\nShieldHer User"
        );
        mailSender.send(message);
    }

    // Notification email
    public void sendAlert(String toEmail, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setSubject("ShieldHer Alert — New Photo Found!");
        mail.setText(message);
        mailSender.send(mail);
    }
}