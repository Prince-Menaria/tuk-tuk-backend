package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserFollow;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.FollowStatus;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    // Check if user A follows user B
    Optional<UserFollow> findByFollowerUserIdAndFollowingUserIdAndFollowStatus(
            Long followerId, Long followingId, FollowStatus status);

    // Get all followers of a user (people who follow this user)
    Page<UserFollow> findByFollowingUserIdAndFollowStatusOrderByFollowedAtDesc(
            Long userId, FollowStatus status, Pageable pageable);

    // Get all following of a user (people this user follows)
    Page<UserFollow> findByFollowerUserIdAndFollowStatusOrderByFollowedAtDesc(
            Long userId, FollowStatus status, Pageable pageable);

    // Count followers
    long countByFollowingUserIdAndFollowStatus(Long userId, FollowStatus status);

    // Count following
    long countByFollowerUserIdAndFollowStatus(Long userId, FollowStatus status);

    // Check if following relationship exists
    boolean existsByFollowerUserIdAndFollowingUserIdAndFollowStatus(
            Long followerId, Long followingId, FollowStatus status);

    // Get mutual followers (users who both follow each other)
    List<UserFollow> findByFollowerUserIdAndFollowStatusAndFollowingIn(
            Long userId, FollowStatus status, List<User> mutualUsers);

    // Get recent followers
    List<UserFollow> findByFollowingUserIdAndFollowStatusAndFollowedAtAfterOrderByFollowedAtDesc(
            Long userId, FollowStatus status, LocalDateTime afterDate);

    // Get follow relationship (any status)
    Optional<UserFollow> findByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    // Delete follow relationship
    void deleteByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    // Get all followers with any status
    List<UserFollow> findByFollowingUserIdOrderByFollowedAtDesc(Long userId);

    // Get all following with any status
    List<UserFollow> findByFollowerUserIdOrderByFollowedAtDesc(Long userId);

}
