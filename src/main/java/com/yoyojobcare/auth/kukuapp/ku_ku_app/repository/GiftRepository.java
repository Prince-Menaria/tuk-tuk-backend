package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Gift;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    
    List<Gift> findByIsActiveTrueOrderByOrderIndexAsc();

    List<Gift> findByCategoryAndIsActiveTrueOrderByOrderIndexAsc(String category);
}
