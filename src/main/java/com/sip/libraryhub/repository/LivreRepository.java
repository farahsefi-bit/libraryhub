package com.sip.libraryhub.repository;
import com.sip.libraryhub.entity.Livre;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface LivreRepository extends JpaRepository<Livre, Long> {

	List<Livre> findByEnVenteTrueAndStockVenteGreaterThan(int i);}