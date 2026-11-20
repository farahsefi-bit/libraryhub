package com.sip.libraryhub.repository;
import com.sip.libraryhub.entity.Role;
import com.sip.libraryhub.entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByLibelle(ERole libelle);
}