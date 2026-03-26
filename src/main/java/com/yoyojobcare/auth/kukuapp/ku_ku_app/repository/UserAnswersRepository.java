package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserAnswers;

public interface UserAnswersRepository extends JpaRepository<UserAnswers, Long> {

    // ✅ Derived query — safest option
    Optional<UserAnswers> findByUserUserId(Long userId);

}
