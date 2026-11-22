package com.sip.libraryhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Livre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private Integer anneePublication;
    private Integer nbExemplaires;
    @Column(length = 1000)
    private String description;

    private String categorie;

    @Column(precision = 10, scale = 2)
    private BigDecimal prix;
    private Boolean enVente = false;
    private Integer stockVente = 0;

    private Long couvertureId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "livre_apercu", joinColumns = @JoinColumn(name = "livre_id"))
    @Column(name = "image_id")
    @OrderColumn(name = "ordre")
    private List<Long> apercuIds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auteur_id")
    private Auteur auteur;
}