// FollowServiceImpl.java
package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.UserFollow;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.FollowStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserFollowRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.FollowService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.FollowUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.GetFollowersRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.UnfollowUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowStatsDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowUserResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowersListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowingListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.UnfollowUserResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.UserBasicInfoDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.UserFollowInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final UserFollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FollowUserResponseDto followUser(Long followerId, FollowUserRequestDto request) {
        if (followerId.equals(request.getUserIdToFollow())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new RuntimeException("Follower user not found"));

        User userToFollow = userRepository.findById(request.getUserIdToFollow())
            .orElseThrow(() -> new RuntimeException("User to follow not found"));

        // Check if already following
        UserFollow existingFollow = followRepository
            .findByFollowerUserIdAndFollowingUserId(followerId, request.getUserIdToFollow())
            .orElse(null);

        boolean isNewFollow = (existingFollow == null);
        
        if (existingFollow != null && existingFollow.getFollowStatus() == FollowStatus.ACTIVE) {
            throw new RuntimeException("You are already following this user");
        }

        UserFollow follow;
        if (existingFollow != null) {
            // Re-activate existing follow
            existingFollow.setFollowStatus(FollowStatus.ACTIVE);
            existingFollow.setFollowedAt(LocalDateTime.now());
            follow = followRepository.save(existingFollow);
        } else {
            // Create new follow
            follow = UserFollow.builder()
                .follower(follower)
                .following(userToFollow)
                .followedAt(LocalDateTime.now())
                .followStatus(FollowStatus.ACTIVE)
                .build();
            follow = followRepository.save(follow);
        }

        log.info("User {} followed user {}", followerId, request.getUserIdToFollow());

        return FollowUserResponseDto.builder()
            .success(true)
            .message("Successfully followed " + userToFollow.getFullName())
            .followId(follow.getFollowId())
            .followedAt(follow.getFollowedAt())
            .followedUser(convertToUserBasicInfo(userToFollow))
            .followStatus(follow.getFollowStatus().name())
            .isNewFollow(isNewFollow)
            
            .build();
    }

    @Override
    @Transactional
    public UnfollowUserResponseDto unfollowUser(Long followerId, UnfollowUserRequestDto request) {
        User userToUnfollow = userRepository.findById(request.getUserIdToUnfollow())
            .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        UserFollow follow = followRepository
            .findByFollowerUserIdAndFollowingUserIdAndFollowStatus(
                followerId, request.getUserIdToUnfollow(), FollowStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("You are not following this user"));

        // Option 1: Delete the follow record completely
        followRepository.delete(follow);
        
        // Option 2: Mark as UNFOLLOWED (to keep history)
        // follow.setFollowStatus(FollowStatus.UNFOLLOWED);
        // followRepository.save(follow);

        log.info("User {} unfollowed user {}", followerId, request.getUserIdToUnfollow());

        return UnfollowUserResponseDto.builder()
            .success(true)
            .message("Successfully unfollowed " + userToUnfollow.getFullName())
            .unfollowedUser(convertToUserBasicInfo(userToUnfollow))
            .unfollowedAt(LocalDateTime.now())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowersListResponseDto getFollowers(Long userId, GetFollowersRequestDto request) {
        Sort sort = Sort.by(
            request.getSortDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
            request.getSortBy()
        );

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        FollowStatus status = request.getStatus().equalsIgnoreCase("ALL") ? 
            null : FollowStatus.valueOf(request.getStatus().toUpperCase());

        Page<UserFollow> followersPage;
        if (status != null) {
            followersPage = followRepository.findByFollowingUserIdAndFollowStatusOrderByFollowedAtDesc(
                userId, status, pageRequest);
        } else {
            // For "ALL" status, we'd need a different query or handle in repository
            followersPage = followRepository.findByFollowingUserIdAndFollowStatusOrderByFollowedAtDesc(
                userId, FollowStatus.ACTIVE, pageRequest);
        }

        List<UserFollowInfoDto> followers = followersPage.getContent().stream()
            .map(follow -> convertToFollowInfo(follow, true, userId))
            .collect(Collectors.toList());

        FollowStatsDto stats = getFollowStats(userId);

        return FollowersListResponseDto.builder()
            .currentPage(request.getPage())
            .pageSize(request.getSize())
            .totalFollowers(followersPage.getTotalElements())
            .totalPages(followersPage.getTotalPages())
            .hasNext(followersPage.hasNext())
            .hasPrevious(followersPage.hasPrevious())
            .followers(followers)
            .stats(stats)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowingListResponseDto getFollowing(Long userId, GetFollowersRequestDto request) {
        Sort sort = Sort.by(
            request.getSortDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
            request.getSortBy()
        );

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        FollowStatus status = request.getStatus().equalsIgnoreCase("ALL") ? 
            null : FollowStatus.valueOf(request.getStatus().toUpperCase());

        Page<UserFollow> followingPage;
        if (status != null) {
            followingPage = followRepository.findByFollowerUserIdAndFollowStatusOrderByFollowedAtDesc(
                userId, status, pageRequest);
        } else {
            followingPage = followRepository.findByFollowerUserIdAndFollowStatusOrderByFollowedAtDesc(
                userId, FollowStatus.ACTIVE, pageRequest);
        }

        List<UserFollowInfoDto> following = followingPage.getContent().stream()
            .map(follow -> convertToFollowInfo(follow, false, userId))
            .collect(Collectors.toList());

        FollowStatsDto stats = getFollowStats(userId);

        return FollowingListResponseDto.builder()
            .currentPage(request.getPage())
            .pageSize(request.getSize())
            .totalFollowing(followingPage.getTotalElements())
            .totalPages(followingPage.getTotalPages())
            .hasNext(followingPage.hasNext())
            .hasPrevious(followingPage.hasPrevious())
            .following(following)
            .stats(stats)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerUserIdAndFollowingUserIdAndFollowStatus(
            followerId, followingId, FollowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatsDto getFollowStats(Long userId) {
        long followersCount = followRepository.countByFollowingUserIdAndFollowStatus(userId, FollowStatus.ACTIVE);
        long followingCount = followRepository.countByFollowerUserIdAndFollowStatus(userId, FollowStatus.ACTIVE);
        
        // Recent followers (last 7 days)
        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        List<UserFollow> recentFollowers = followRepository
            .findByFollowingUserIdAndFollowStatusAndFollowedAtAfterOrderByFollowedAtDesc(
                userId, FollowStatus.ACTIVE, weekAgo);

        return FollowStatsDto.builder()
            .totalFollowers(followersCount)
            .totalFollowing(followingCount)
            .mutualFollows(0L) // TODO: Implement mutual follows calculation
            .recentFollowersCount((long) recentFollowers.size())
            .isPrivateAccount(false) // TODO: Implement based on user settings
            .isFollowRequestPending(false) // TODO: Implement for private accounts
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowingListResponseDto getMutualFollows(Long userId1, Long userId2, GetFollowersRequestDto request) {
        // TODO: Implement complex query to find mutual follows
        // For now, return empty result
        return FollowingListResponseDto.builder()
            .currentPage(0)
            .pageSize(0)
            .totalFollowing(0L)
            .totalPages(0)
            .hasNext(false)
            .hasPrevious(false)
            .following(List.of())
            .stats(getFollowStats(userId1))
            .build();
    }

    @Override
    @Transactional
    public UnfollowUserResponseDto removeFollower(Long userId, UnfollowUserRequestDto request) {
        User followerUser = userRepository.findById(request.getUserIdToUnfollow())
            .orElseThrow(() -> new RuntimeException("Follower user not found"));

        UserFollow follow = followRepository
            .findByFollowerUserIdAndFollowingUserIdAndFollowStatus(
                request.getUserIdToUnfollow(), userId, FollowStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("This user is not following you"));

        // Remove follower
        followRepository.delete(follow);

        log.info("User {} removed follower {}", userId, request.getUserIdToUnfollow());

        return UnfollowUserResponseDto.builder()
            .success(true)
            .message("Successfully removed " + followerUser.getFullName() + " from followers")
            .unfollowedUser(convertToUserBasicInfo(followerUser))
            .unfollowedAt(LocalDateTime.now())
            .build();
    }

    // Helper methods
    private UserFollowInfoDto convertToFollowInfo(UserFollow follow, boolean isFollowersList, Long currentUserId) {
        User targetUser = isFollowersList ? follow.getFollower() : follow.getFollowing();
        
        // Check if following back (for followers list)
        boolean isFollowingBack = false;
        if (isFollowersList) {
            isFollowingBack = isFollowing(currentUserId, targetUser.getUserId());
        }

        return UserFollowInfoDto.builder()
            .followId(follow.getFollowId())
            .user(convertToUserBasicInfo(targetUser))
            .followStatus(follow.getFollowStatus().name())
            .followedAt(follow.getFollowedAt())
            .timeAgo(calculateTimeAgo(follow.getFollowedAt()))
            .isFollowingBack(isFollowingBack)
            // .isVerified(targetUser.getIsVerified() != null ? targetUser.getIsVerified() : false)
            // .lastSeen(calculateLastSeen(targetUser.getLastSeenAt()))
            .build();
    }

    private UserBasicInfoDto convertToUserBasicInfo(User user) {
        return UserBasicInfoDto.builder()
            .userId(user.getUserId())
            .fullName(user.getFullName())
            // .username(user.getUsername())
            .profileImage(user.getImage())
            // .isVerified(user.getIsVerified() != null ? user.getIsVerified() : false)
            // .isOnline(user.getIsOnline() != null ? user.getIsOnline() : false)
            // .lastSeen(calculateLastSeen(user.getLastSeenAt()))
            // .bio(user.getBio())
            // .level(user.getLevel() != null ? user.getLevel() : 1)
            .build();
    }

    private String calculateTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + " hours ago";
        
        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7) return days + " days ago";
        
        long weeks = days / 7;
        if (weeks < 4) return weeks + " weeks ago";
        
        long months = days / 30;
        return months + " months ago";
    }

    private String calculateLastSeen(LocalDateTime lastSeenAt) {
        if (lastSeenAt == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(lastSeenAt, now);
        
        if (minutes < 5) return "Online";
        if (minutes < 60) return "Last seen " + minutes + " minutes ago";
        
        long hours = ChronoUnit.HOURS.between(lastSeenAt, now);
        if (hours < 24) return "Last seen " + hours + " hours ago";
        
        long days = ChronoUnit.DAYS.between(lastSeenAt, now);
        return "Last seen " + days + " days ago";
    }
}