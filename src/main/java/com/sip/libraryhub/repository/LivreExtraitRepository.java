package com.sip.libraryhub.repository;

import com.sip.libraryhub.entity.LivreExtrait;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LivreExtraitRepository extends JpaRepository<LivreExtrait, Long> {
    Optional<LivreExtrait> findByLivreId(Long livreId);
}
