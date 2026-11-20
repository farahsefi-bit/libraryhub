package com.sip.libraryhub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender sender;
    @Value("${app.admin.email}") private String adminEmail;
    @Value("${spring.mail.username:}") private String from;

    public MailService(JavaMailSender sender) { this.sender = sender; }

    @Async
    public void notifyAdminNewUser(String nom, String prenom, String email) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            if (!from.isBlank()) m.setFrom(from);
            m.setTo(adminEmail);
            m.setSubject("LibraryHub : nouvelle inscription");
            m.setText("Un nouvel utilisateur s'est inscrit : " + prenom + " " + nom + " (" + email + ").");
            sender.send(m);
        } catch (Exception e) {
            log.warn("Envoi d'email impossible : {}", e.getMessage());
        }
    }
}
