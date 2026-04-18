package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ✅ User ke posts — latest first
    Page<Post> findByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long userId, Pageable pageable);

    // ✅ Feed — all active posts
    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}
