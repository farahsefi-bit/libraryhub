package com.sip.libraryhub.service;

import com.sip.libraryhub.entity.ImageLivre;
import com.sip.libraryhub.entity.Livre;
import com.sip.libraryhub.repository.ImageLivreRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageService {

    private static final int COTE_MAX = 1000;

    private final ImageLivreRepository repo;

    public ImageService(ImageLivreRepository repo) {
        this.repo = repo;
    }

    /** Redimensionne l'image (1000 px max) et l'enregistre en JPEG dans la base. */
    public ImageLivre enregistrer(MultipartFile fichier) throws IOException {
        BufferedImage source = ImageIO.read(fichier.getInputStream());
        if (source == null) {
            throw new IOException("Format d'image non pris en charge (utilisez JPG, PNG ou GIF)");
        }
        double ratio = Math.min(1.0, (double) COTE_MAX / Math.max(source.getWidth(), source.getHeight()));
        int w = Math.max(1, (int) Math.round(source.getWidth() * ratio));
        int h = Math.max(1, (int) Math.round(source.getHeight() * ratio));

        BufferedImage cible = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = cible.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, w, h, null);
        g.dispose();

        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        ImageIO.write(cible, "jpg", sortie);

        ImageLivre image = new ImageLivre();
        String nom = fichier.getOriginalFilename();
        image.setNom(nom == null ? "image" : nom.substring(0, Math.min(200, nom.length())));
        image.setContentType("image/jpeg");
        image.setData(sortie.toByteArray());
        return repo.save(image);
    }

    public void supprimer(Long id) {
        if (id != null) {
            repo.deleteById(id);
        }
    }

    public void supprimerImages(Livre livre) {
        supprimer(livre.getCouvertureId());
        for (Long id : livre.getApercuIds()) {
            supprimer(id);
        }
    }
}