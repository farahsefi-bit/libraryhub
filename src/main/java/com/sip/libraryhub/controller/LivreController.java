package com.sip.libraryhub.controller;

import com.sip.libraryhub.entity.Livre;
import com.sip.libraryhub.repository.LivreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/livres")
public class LivreController {
    private final LivreRepository repo;
    public LivreController(LivreRepository repo) { this.repo = repo; }

    @GetMapping public List<Livre> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Livre> getOne(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping public Livre create(@RequestBody Livre l) { l.setId(null); return repo.save(l); }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Livre> update(@PathVariable Long id, @RequestBody Livre l) {
        return repo.findById(id).map(old -> {
            old.setTitre(l.getTitre()); old.setAnneePublication(l.getAnneePublication());
            old.setNbExemplaires(l.getNbExemplaires()); old.setDescription(l.getDescription());
            old.setAuteur(l.getAuteur());
            return ResponseEntity.ok(repo.save(old));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id); return ResponseEntity.noContent().build();
    }
}