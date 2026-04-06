package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserVisitor;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.VisitorType;

public interface UserVisitorRepository extends JpaRepository<UserVisitor, Long> {

    // Get visitors of a specific user's profile
    Page<UserVisitor> findByProfileOwnerUserIdOrderByLastVisitAtDesc(
        Long profileOwnerId, Pageable pageable);

    // Get specific visitor record
    Optional<UserVisitor> findByProfileOwnerUserIdAndVisitorUserId(
        Long profileOwnerId, Long visitorUserId);

    // Count total unique visitors
    long countByProfileOwnerUserId(Long profileOwnerId);

    // Get visitors by type
    List<UserVisitor> findByProfileOwnerUserIdAndVisitorTypeOrderByLastVisitAtDesc(
        Long profileOwnerId, VisitorType visitorType);

    // Get recent visitors (within specific time)
    List<UserVisitor> findByProfileOwnerUserIdAndLastVisitAtAfterOrderByLastVisitAtDesc(
        Long profileOwnerId, LocalDateTime afterDate);

    // Get visitors from specific source
    List<UserVisitor> findByProfileOwnerUserIdAndSourcePageOrderByLastVisitAtDesc(
        Long profileOwnerId, String sourcePage);

    // Get top visitors by visit count
    List<UserVisitor> findByProfileOwnerUserIdOrderByVisitCountDescLastVisitAtDesc(
        Long profileOwnerId, Pageable pageable);

    // Check if user has visited profile
    boolean existsByProfileOwnerUserIdAndVisitorUserId(Long profileOwnerId, Long visitorUserId);

    // Get all visits by a specific visitor
    List<UserVisitor> findByVisitorUserIdOrderByLastVisitAtDesc(Long visitorUserId);

    // Delete old visitor records (for cleanup)
    void deleteByProfileOwnerUserIdAndLastVisitAtBefore(Long profileOwnerId, LocalDateTime beforeDate);
}
