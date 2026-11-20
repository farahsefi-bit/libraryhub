package com.sip.libraryhub.config;

import com.sip.libraryhub.entity.*;
import com.sip.libraryhub.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roles; private final UserRepository users; private final PasswordEncoder enc;
    @Value("${app.admin.email}") private String adminEmail;
    @Value("${app.admin.password}") private String adminPassword;

    public DataInitializer(RoleRepository roles, UserRepository users, PasswordEncoder enc) {
        this.roles = roles; this.users = users; this.enc = enc;
    }

    @Override
    public void run(String... args) {
        for (ERole r : ERole.values())
            if (roles.findByLibelle(r).isEmpty()) roles.save(new Role(null, r));

        if (!users.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail); admin.setPassword(enc.encode(adminPassword));
            admin.setNom("Admin"); admin.setPrenom("Super"); admin.setTel("00000000");
            admin.setRoles(Set.of(roles.findByLibelle(ERole.ROLE_ADMIN).orElseThrow()));
            users.save(admin);
        }
    }
}