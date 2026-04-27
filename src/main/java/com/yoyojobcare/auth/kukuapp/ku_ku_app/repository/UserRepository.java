package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByMobile(Long mobile);

}
