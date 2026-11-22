package com.sip.libraryhub.config;

import com.sip.libraryhub.entity.*;
import com.sip.libraryhub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;


    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final AuteurRepository auteurRepo;
    private final LivreRepository livreRepo;
    private final ImageLivreRepository imageRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(RoleRepository roleRepo, UserRepository userRepo,
                           AuteurRepository auteurRepo, LivreRepository livreRepo,
                           ImageLivreRepository imageRepo, PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.auteurRepo = auteurRepo;
        this.livreRepo = livreRepo;
        this.imageRepo = imageRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try { jdbcTemplate.execute("ALTER TABLE role DROP CONSTRAINT IF EXISTS role_libelle_check"); } catch (Exception ignored) {}

        for (ERole er : ERole.values()) {
            if (roleRepo.findByLibelle(er).isEmpty()) {
                Role r = new Role();
                r.setLibelle(er);
                roleRepo.save(r);
            }
        }

        if (userRepo.findByEmail("admin@libraryhub.tn").isEmpty()) {
            User admin = new User();
            admin.setNom("Admin");
            admin.setPrenom("LibraryHub");
            admin.setEmail("admin@libraryhub.tn");
            admin.setPassword(encoder.encode("Admin123!"));
            admin.setTel("71000111");
            admin.setRoles(Set.of(roleRepo.findByLibelle(ERole.ROLE_ADMIN).orElseThrow()));
            userRepo.save(admin);
        }

        if (userRepo.findByEmail("client@libraryhub.tn").isEmpty()) {
            User client = new User();
            client.setNom("Ben Ali");
            client.setPrenom("Sami");
            client.setEmail("client@libraryhub.tn");
            client.setPassword(encoder.encode("Client123!"));
            client.setTel("22333444");
            client.setRoles(Set.of(roleRepo.findByLibelle(ERole.ROLE_CLIENT).orElseThrow()));
            userRepo.save(client);
        }

        if (livreRepo.count() == 0) {
            Auteur a1 = creerAuteur("Martin", "Robert C.", "Américaine", "Ingénieur logiciel et auteur de référence sur l'architecture propre et le Clean Code.");
            Auteur a2 = creerAuteur("Hugo", "Victor", "Française", "Écrivain, poète et dramaturge majeur de la littérature française du XIXe siècle.");
            Auteur a3 = creerAuteur("Kleppmann", "Martin", "Allemande", "Chercheur en systèmes distribués à l'Université de Cambridge.");
            Auteur a4 = creerAuteur("Maalouf", "Amin", "Libano-française", "Écrivain franco-libanais, membre de l'Académie française.");

            creerLivreDemo(
                "Clean Architecture", 2017, 12,
                "Guide pratique de conception logicielle pour bâtir des systèmes maintenables, testables et indépendants des frameworks.",
                "Informatique", new BigDecimal("68.50"), true, 15, a1,
                new Color(30, 58, 138), new Color(15, 23, 42),
                List.of("Chapitre 1 : Qu'est-ce que l'architecture ?", "Chapitre 2 : Les paradigmes de programmation")
            );

            creerLivreDemo(
                "Designing Data-Intensive Applications", 2018, 8,
                "Les fondements des bases de données modernes, de la réplication, du partitionnement et du traitement distribué.",
                "Informatique", new BigDecimal("84.00"), true, 9, a3,
                new Color(88, 28, 135), new Color(17, 24, 39),
                List.of("Chapitre 1 : Fiabilité, scalabilité et maintenabilité", "Chapitre 2 : Modèles de données et langages de requête")
            );

            creerLivreDemo(
                "Les Misérables", 1862, 20,
                "Fresque sociale et humaniste suivant le destin de Jean Valjean dans la France du XIXe siècle.",
                "Littérature", new BigDecimal("34.00"), true, 22, a2,
                new Color(127, 29, 29), new Color(24, 24, 27),
                List.of("Livre Premier : Un juste — Monseigneur Myriel", "Livre Deuxième : La chute — Le soir d'un jour de marche")
            );

            creerLivreDemo(
                "Léon l'Africain", 1986, 14,
                "Roman historique retraçant le périple fascinant de Hassan al-Wazzan entre Grenade, Fès, Tombouctou et Rome.",
                "Histoire & Roman", new BigDecimal("29.90"), true, 18, a4,
                new Color(146, 64, 14), new Color(28, 25, 23),
                List.of("Livre de Grenade : L'année de la chute", "Livre de Fès : Les années d'apprentissage")
            );

            creerLivreDemo(
                "Clean Code (Archive Bibliothèque)", 2008, 5,
                "Exemplaire réservé exclusivement à la consultation sur place en bibliothèque.",
                "Informatique", null, false, 0, a1,
                new Color(6, 95, 70), new Color(17, 24, 39),
                List.of("Chapitre 1 : Code propre")
            );
        }
    }

    private Auteur creerAuteur(String nom, String prenom, String nat, String bio) {
        Auteur a = new Auteur();
        a.setNom(nom);
        a.setPrenom(prenom);
        a.setNationalite(nat);
        a.setBiographie(bio);
        return auteurRepo.save(a);
    }

    private void creerLivreDemo(String titre, int annee, int exemplaires, String desc, String cat,
                                BigDecimal prix, boolean enVente, int stock, Auteur auteur,
                                Color c1, Color c2, List<String> extraits) throws Exception {
        Livre l = new Livre();
        l.setTitre(titre);
        l.setAnneePublication(annee);
        l.setNbExemplaires(exemplaires);
        l.setDescription(desc);
        l.setCategorie(cat);
        l.setPrix(prix);
        l.setEnVente(enVente);
        l.setStockVente(stock);
        l.setAuteur(auteur);

        ImageLivre couv = genererImageCouverture(titre, auteur.getPrenom() + " " + auteur.getNom(), cat, c1, c2);
        l.setCouvertureId(couv.getId());

        int pageNum = 1;
        for (String extrait : extraits) {
            ImageLivre page = genererPageApercu(titre, pageNum++, extrait, desc);
            l.getApercuIds().add(page.getId());
        }
        livreRepo.save(l);
    }

    private ImageLivre genererImageCouverture(String titre, String auteur, String cat, Color c1, Color c2) throws Exception {
        int w = 600, h = 850;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(255, 255, 255, 35));
        g.drawRect(30, 30, w - 60, h - 60);

        g.setColor(new Color(251, 191, 36));
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("LIBRARYHUB • " + cat.toUpperCase(), 55, 85);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.BOLD, 36));
        dessinerTexteMultiLigne(g, titre, 55, 220, w - 110, 46);

        g.setColor(new Color(203, 213, 225));
        g.setFont(new Font("SansSerif", Font.PLAIN, 22));
        g.drawString(auteur, 55, h - 90);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        ImageLivre il = new ImageLivre();
        il.setNom("cover.jpg");
        il.setContentType("image/jpeg");
        il.setData(out.toByteArray());
        return imageRepo.save(il);
    }

    private ImageLivre genererPageApercu(String titre, int num, String chapitre, String contenu) throws Exception {
        int w = 650, h = 900;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(250, 248, 243));
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(100, 116, 139));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString(titre + " — Extrait officiel", 60, 60);
        g.drawString("Page " + num, w - 120, 60);
        g.drawLine(60, 75, w - 60, 75);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Serif", Font.BOLD, 24));
        dessinerTexteMultiLigne(g, chapitre, 60, 140, w - 120, 32);

        g.setColor(new Color(51, 65, 85));
        g.setFont(new Font("Serif", Font.PLAIN, 18));
        String paragraphe = contenu + "\n\nCet aperçu est mis à disposition par LibraryHub afin de permettre aux lecteurs de découvrir la mise en page, le style et la table des matières avant l'achat en ligne.";
        dessinerTexteMultiLigne(g, paragraphe, 60, 240, w - 120, 28);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        ImageLivre il = new ImageLivre();
        il.setNom("page_" + num + ".jpg");
        il.setContentType("image/jpeg");
        il.setData(out.toByteArray());
        return imageRepo.save(il);
    }

    private void dessinerTexteMultiLigne(Graphics2D g, String texte, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g.getFontMetrics();
        for (String bloc : texte.split("\n")) {
            StringBuilder ligne = new StringBuilder();
            for (String mot : bloc.split(" ")) {
                if (fm.stringWidth(ligne + mot) > maxW) {
                    g.drawString(ligne.toString(), x, y);
                    y += lineH;
                    ligne = new StringBuilder(mot + " ");
                } else {
                    ligne.append(mot).append(" ");
                }
            }
            g.drawString(ligne.toString(), x, y);
            y += lineH;
        }
    }
}