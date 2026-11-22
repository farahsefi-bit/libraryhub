package com.sip.libraryhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
@Getter @Setter @NoArgsConstructor
public class LigneCommande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    @JsonIgnore
    private Commande commande;

    private Long livreId;
    private String titre;
    private Integer quantite;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixUnitaire;
}