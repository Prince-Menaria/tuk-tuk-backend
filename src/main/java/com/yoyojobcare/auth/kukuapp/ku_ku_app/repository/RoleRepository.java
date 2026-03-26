package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}