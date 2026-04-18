package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByPostPostIdAndUserUserId(Long postId, Long userId);

    boolean existsByPostPostIdAndUserUserId(Long postId, Long userId);
}
