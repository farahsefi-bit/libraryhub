package com.sip.libraryhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Auteur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String nationalite;
    @Column(length = 1000)
    private String biographie;

    @OneToMany(mappedBy = "auteur", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Livre> livres = new ArrayList<>();
}