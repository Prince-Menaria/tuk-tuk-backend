package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.FollowUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.GetFollowersRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.UnfollowUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowStatsDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowUserResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowersListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowingListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.UnfollowUserResponseDto;

public interface FollowService {

    // Follow/Unfollow operations
    FollowUserResponseDto followUser(Long followerId, FollowUserRequestDto request);

    UnfollowUserResponseDto unfollowUser(Long followerId, UnfollowUserRequestDto request);

    // Get followers and following lists
    FollowersListResponseDto getFollowers(Long userId, GetFollowersRequestDto request);

    FollowingListResponseDto getFollowing(Long userId, GetFollowersRequestDto request);

    // Check follow status
    boolean isFollowing(Long followerId, Long followingId);

    FollowStatsDto getFollowStats(Long userId);

    // Mutual follows
    FollowingListResponseDto getMutualFollows(Long userId1, Long userId2, GetFollowersRequestDto request);

    // Remove follower (block functionality)
    UnfollowUserResponseDto removeFollower(Long userId, UnfollowUserRequestDto request);

}
