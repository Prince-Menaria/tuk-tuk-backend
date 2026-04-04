package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserInterest;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // ✅ Derived query — JPQL automatically banti hai, koi issue nahi
    List<UserInterest> findByUserUserId(Long userId);

    void deleteByUserUserId(Long userId);

}
