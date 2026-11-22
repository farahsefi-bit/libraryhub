package com.sip.libraryhub.controller;

import com.sip.libraryhub.entity.Livre;
import com.sip.libraryhub.repository.AuteurRepository;
import com.sip.libraryhub.repository.LivreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final LivreRepository livres;
    private final AuteurRepository auteurs;

    public PublicController(LivreRepository livres, AuteurRepository auteurs) {
        this.livres = livres;
        this.auteurs = auteurs;
    }

    @GetMapping("/livres")
    public List<Livre> livresEnVente() {
        return livres.findByEnVenteTrueAndStockVenteGreaterThan(0);
    }

    @GetMapping("/livres/{id}")
    public ResponseEntity<Livre> livre(@PathVariable Long id) {
        return livres.findById(id)
            .filter(l -> Boolean.TRUE.equals(l.getEnVente()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
            "livres", livres.count(),
            "auteurs", auteurs.count(),
            "enVente", (long) livres.findByEnVenteTrueAndStockVenteGreaterThan(0).size()
        );
    }
}