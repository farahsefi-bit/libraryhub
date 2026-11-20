package com.sip.libraryhub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String NOTIFICATION_EMAIL = "farah.sefi@ensi-uma.tn";

    private final JavaMailSender sender;

    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }

    @Async
    public void notifyAdminNewUser(String nom, String prenom, String email) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(NOTIFICATION_EMAIL);
            m.setTo(NOTIFICATION_EMAIL);
            m.setSubject("LibraryHub : nouvelle inscription");
            m.setText("Un nouvel utilisateur s'est inscrit sur LibraryHub :\n\n"
                    + "Nom complet : " + prenom + " " + nom + "\n"
                    + "Email : " + email);
            sender.send(m);
            log.info("Email de notification envoyé à {} pour l'inscription de {}", NOTIFICATION_EMAIL, email);
        } catch (Exception e) {
            log.warn("Envoi d'email impossible : {}", e.getMessage());
        }
    }
}