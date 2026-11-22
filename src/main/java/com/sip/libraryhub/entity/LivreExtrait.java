package com.sip.libraryhub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "livre_extraits")
public class LivreExtrait {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;

    @Lob
    @Column(name = "pdf_data", columnDefinition = "BYTEA")
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "livre_id", unique = true)
    private Livre livre;

    // Getters et Setters
}
