package com.sip.libraryhub.repository;
import com.sip.libraryhub.entity.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LivreRepository extends JpaRepository<Livre, Long> {}