package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.StoreItem;

public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {

    // ✅ Main category se items
    List<StoreItem> findByMainCategoryAndIsActiveTrueOrderByOrderIndexAsc(String mainCategory);

    // ✅ Main + sub category
    List<StoreItem> findByMainCategoryAndSubCategoryAndIsActiveTrueOrderByOrderIndexAsc(
        String mainCategory, String subCategory);
}

