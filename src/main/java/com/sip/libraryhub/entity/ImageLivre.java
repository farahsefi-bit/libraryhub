package com.sip.libraryhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images_livres")
@Getter @Setter @NoArgsConstructor
public class ImageLivre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String contentType;

    @JsonIgnore
    @Column(length = 8000000)
    private byte[] data;
}