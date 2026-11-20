package com.sip.libraryhub.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class AuthDtos {
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min = 6) String password,
                                  String tel, @NotBlank String nom, @NotBlank String prenom) {}
    public record AuthResponse(String token, Long id, String email, String nom, String prenom, List<String> roles) {}
}