package com.sip.libraryhub.controller;

import com.sip.libraryhub.entity.ImageLivre;
import com.sip.libraryhub.entity.Livre;
import com.sip.libraryhub.repository.LivreRepository;
import com.sip.libraryhub.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livres")
@PreAuthorize("hasAnyRole('ADMIN','COLLABORATEUR')")
public class LivreController {

    private static final int MAX_APERCU = 8;

    private final LivreRepository repo;
    private final ImageService images;

    public LivreController(LivreRepository repo, ImageService images) {
        this.repo = repo;
        this.images = images;
    }

    @GetMapping
    public List<Livre> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livre> getOne(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Livre create(@RequestBody Livre l) {
        l.setId(null);
        l.setCouvertureId(null);
        l.setApercuIds(new ArrayList<>());
        normaliser(l);
        return repo.save(l);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Livre> update(@PathVariable Long id, @RequestBody Livre l) {
        return repo.findById(id).map(old -> {
            old.setTitre(l.getTitre());
            old.setAnneePublication(l.getAnneePublication());
            old.setNbExemplaires(l.getNbExemplaires());
            old.setDescription(l.getDescription());
            old.setCategorie(l.getCategorie());
            old.setPrix(l.getPrix());
            old.setEnVente(l.getEnVente());
            old.setStockVente(l.getStockVente());
            old.setAuteur(l.getAuteur());
            normaliser(old);
            return ResponseEntity.ok(repo.save(old));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Livre l = repo.findById(id).orElse(null);
        if (l == null) return ResponseEntity.notFound().build();
        images.supprimerImages(l);
        repo.delete(l);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/couverture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> couverture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Livre l = repo.findById(id).orElse(null);
        if (l == null) return ResponseEntity.notFound().build();
        try {
            ImageLivre img = images.enregistrer(file);
            Long ancienne = l.getCouvertureId();
            l.setCouvertureId(img.getId());
            repo.save(l);
            images.supprimer(ancienne);
            return ResponseEntity.ok(l);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Image invalide : JPG, PNG ou GIF uniquement"));
        }
    }

    @PostMapping(value = "/{id}/apercu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> apercu(@PathVariable Long id, @RequestParam("files") List<MultipartFile> files) {
        Livre l = repo.findById(id).orElse(null);
        if (l == null) return ResponseEntity.notFound().build();
        if (l.getApercuIds().size() + files.size() > MAX_APERCU) {
            return ResponseEntity.badRequest().body(Map.of("message", "Maximum " + MAX_APERCU + " pages d'aperçu par livre"));
        }
        try {
            for (MultipartFile f : files) {
                ImageLivre img = images.enregistrer(f);
                l.getApercuIds().add(img.getId());
            }
            return ResponseEntity.ok(repo.save(l));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Image invalide : JPG, PNG ou GIF uniquement"));
        }
    }

    @DeleteMapping("/{id}/apercu/{imageId}")
    public ResponseEntity<?> supprimerApercu(@PathVariable Long id, @PathVariable Long imageId) {
        Livre l = repo.findById(id).orElse(null);
        if (l == null) return ResponseEntity.notFound().build();
        if (l.getApercuIds().remove(imageId)) {
            repo.save(l);
            images.supprimer(imageId);
        }
        return ResponseEntity.ok(l);
    }

    private void normaliser(Livre l) {
        if (l.getStockVente() == null) l.setStockVente(0);
        if (l.getEnVente() == null) l.setEnVente(false);
        if (l.getPrix() == null || l.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            l.setPrix(null);
            l.setEnVente(false);
        }
    }
}