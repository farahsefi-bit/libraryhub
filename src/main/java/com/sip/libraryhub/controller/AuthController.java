package com.sip.libraryhub.controller;

import com.sip.libraryhub.dto.AuthDtos.*;
import com.sip.libraryhub.entity.*;
import com.sip.libraryhub.repository.*;
import com.sip.libraryhub.security.JwtService;
import com.sip.libraryhub.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager; private final UserRepository users;
    private final RoleRepository roles; private final PasswordEncoder enc;
    private final JwtService jwt; private final MailService mail;

    public AuthController(AuthenticationManager am, UserRepository u, RoleRepository r,
                          PasswordEncoder e, JwtService j, MailService m) {
        this.authManager = am; this.users = u; this.roles = r; this.enc = e; this.jwt = j; this.mail = m;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email()))
            return ResponseEntity.badRequest().body(Map.of("message", "Email déjà utilisé"));
        User u = new User();
        u.setEmail(req.email()); u.setPassword(enc.encode(req.password()));
        u.setTel(req.tel()); u.setNom(req.nom()); u.setPrenom(req.prenom());
        u.setRoles(Set.of(roles.findByLibelle(ERole.ROLE_COLLABORATEUR).orElseThrow()));
        users.save(u);
        mail.notifyAdminNewUser(req.nom(), req.prenom(), req.email());
        return ResponseEntity.ok(Map.of("message", "Inscription réussie"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect"));
        }
        User u = users.findByEmail(req.email()).orElseThrow();
        UserDetails details = org.springframework.security.core.userdetails.User
            .withUsername(u.getEmail()).password(u.getPassword()).authorities("x").build();
        String token = jwt.generateToken(details);
        List<String> r = u.getRoles().stream().map(x -> x.getLibelle().name()).toList();
        return ResponseEntity.ok(new AuthResponse(token, u.getId(), u.getEmail(), u.getNom(), u.getPrenom(), r));
    }
}
