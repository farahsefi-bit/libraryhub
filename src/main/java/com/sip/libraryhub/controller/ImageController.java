package com.sip.libraryhub.controller;

import com.sip.libraryhub.repository.ImageLivreRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageLivreRepository repo;

    public ImageController(ImageLivreRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable Long id) {
        return repo.findById(id)
            .map(i -> ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(i.getContentType()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(i.getData()))
            .orElse(ResponseEntity.notFound().build());
    }
}