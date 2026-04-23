package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserInventory;

public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {

    // ✅ User ka inventory
    @Query("""
        SELECT i FROM UserInventory i
        JOIN FETCH i.item
        WHERE i.user.userId = :userId
        AND i.isExpired = false
        ORDER BY i.purchasedAt DESC
        """)
    List<UserInventory> findActiveInventory(@Param("userId") Long userId);

    // ✅ User ne ye item kharida hai?
    boolean existsByUserUserIdAndItemItemIdAndIsExpiredFalse(Long userId, Long itemId);

    // ✅ Category ke equipped items
    @Query("""
        SELECT i FROM UserInventory i
        JOIN FETCH i.item
        WHERE i.user.userId = :userId
        AND i.item.mainCategory = :category
        AND i.isEquipped = true
        AND i.isExpired = false
        """)
    Optional<UserInventory> findEquippedByCategory(
        @Param("userId") Long userId,
        @Param("category") String category);
}
