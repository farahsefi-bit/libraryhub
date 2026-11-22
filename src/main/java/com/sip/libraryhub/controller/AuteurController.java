package com.sip.libraryhub.controller;

import com.sip.libraryhub.entity.Auteur;
import com.sip.libraryhub.repository.AuteurRepository;
import com.sip.libraryhub.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auteurs")
@PreAuthorize("hasAnyRole('ADMIN','COLLABORATEUR')")
public class AuteurController {
    private final AuteurRepository repo;
    private final ImageService images;

    public AuteurController(AuteurRepository repo, ImageService images) {
        this.repo = repo;
        this.images = images;
    }

    @GetMapping
    public List<Auteur> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Auteur> getOne(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Auteur create(@RequestBody Auteur a) { a.setId(null); return repo.save(a); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Auteur> update(@PathVariable Long id, @RequestBody Auteur a) {
        return repo.findById(id).map(old -> {
            old.setNom(a.getNom());
            old.setPrenom(a.getPrenom());
            old.setNationalite(a.getNationalite());
            old.setBiographie(a.getBiographie());
            return ResponseEntity.ok(repo.save(old));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Auteur a = repo.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();
        a.getLivres().forEach(images::supprimerImages);
        repo.delete(a);
        return ResponseEntity.noContent().build();
    }
}