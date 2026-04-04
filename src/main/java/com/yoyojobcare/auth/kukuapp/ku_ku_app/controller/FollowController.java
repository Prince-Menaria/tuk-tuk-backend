package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.FollowService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.FollowUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.GetFollowersRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.UnfollowUserRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowStatsDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowUserResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowersListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.FollowingListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.UnfollowUserResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/social/follow")
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final FollowService followService;
    // private final CurrentUserService currentUserService;

    /**
     * Follow a user
     */
    @PostMapping("/follow")
    public ResponseEntity<MobileResponse<FollowUserResponseDto>> followUser(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long currentUserId,
            @RequestBody FollowUserRequestDto request) {

        try {
            // Long currentUserId = currentUserService.getCurrentUserId(currentUser);
            log.info("👤 User {} following user {}", currentUserId, request.getUserIdToFollow());

            FollowUserResponseDto serviceResponse = followService.followUser(currentUserId, request);

            MobileResponse<FollowUserResponseDto> response = new MobileResponse<>();
            response.setData(serviceResponse);
            response.setMessage("Follow user save successful ..");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Error following user follow: {}", e);
            MobileResponse<FollowUserResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Unfollow a user
     */
    @PostMapping("/unfollow")
    public ResponseEntity<MobileResponse<UnfollowUserResponseDto>> unfollowUser(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long currentUserId,
            @Valid @RequestBody UnfollowUserRequestDto request) {
        
        try {
            // Long currentUserId = currentUserService.getCurrentUserId(currentUser);
            log.info("👤 User {} unfollowing user {}", currentUserId, request.getUserIdToUnfollow());
            
            UnfollowUserResponseDto serviceResponse = followService.unfollowUser(currentUserId, request);
            
            MobileResponse<UnfollowUserResponseDto> response = new MobileResponse<>();
            response.setData(serviceResponse);
            response.setMessage("Unfollow user save successful ..");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("❌ Error unfollowing user: {}", e);
            MobileResponse<UnfollowUserResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get followers list
     */
    @GetMapping("/followers")
    public ResponseEntity<MobileResponse<FollowersListResponseDto>> getFollowers(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long targetUserId,
            @RequestParam(required = false) Long userId, // If null, use current user
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "followedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        
        try {
            // Long targetUserId = userId != null ? userId : currentUserService.getCurrentUserId(currentUser);
            log.info("📋 Getting followers for user: {}", targetUserId);
            
            GetFollowersRequestDto request = new GetFollowersRequestDto();
            request.setPage(page);
            request.setSize(size);
            request.setSortBy(sortBy);
            request.setSortDirection(sortDirection);
            request.setStatus(status);
            
            FollowersListResponseDto serviceResponse = followService.getFollowers(targetUserId, request);
                        
            MobileResponse<FollowersListResponseDto> response = new MobileResponse<>();
            response.setData(serviceResponse);
            response.setMessage("Followers retrieved successfully");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);    
            
        } catch (Exception e) {
            log.error("❌ Error getting followers: {}", e);
            MobileResponse<FollowersListResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get following list
     */
    @GetMapping("/following")
    public ResponseEntity<MobileResponse<FollowingListResponseDto>> getFollowing(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long targetUserId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "followedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        
        try {
            // Long targetUserId = userId != null ? userId : currentUserService.getCurrentUserId(currentUser);
            log.info("📋 Getting following list for user: {}", targetUserId);
            
            GetFollowersRequestDto request = new GetFollowersRequestDto();
            request.setPage(page);
            request.setSize(size);
            request.setSortBy(sortBy);
            request.setSortDirection(sortDirection);
            request.setStatus(status);
            
            FollowingListResponseDto serviceResponse = followService.getFollowing(targetUserId, request);
            
            MobileResponse<FollowingListResponseDto> response = new MobileResponse<>();
            response.setData(serviceResponse);
            response.setMessage("Following list retrieved successfully");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);      
            
        } catch (Exception e) {
            log.error("❌ Error getting following list: {}", e);
            MobileResponse<FollowingListResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if following a user
     */
    @GetMapping("/is-following")
    public ResponseEntity<MobileResponse<Boolean>> isFollowing(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long currentUserId,
            @RequestParam Long userId) {
        
        try {
            // Long currentUserId = currentUserService.getCurrentUserId(currentUser);
            boolean isFollowing = followService.isFollowing(currentUserId, userId);
            
            MobileResponse<Boolean> response = new MobileResponse<>();
            response.setData(isFollowing);
            response.setMessage("Follow status retrieved successfully");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("❌ Error checking follow status: {}", e);
            MobileResponse<Boolean> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get follow stats
     */
    @GetMapping("/stats")
    public ResponseEntity<MobileResponse<FollowStatsDto>> getFollowStats(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long targetUserId,
            @RequestParam(required = false) Long userId) {
        
        try {
            // Long targetUserId = userId != null ? userId : currentUserService.getCurrentUserId(currentUser);
            FollowStatsDto stats = followService.getFollowStats(targetUserId);
                        
            MobileResponse<FollowStatsDto> response = new MobileResponse<>();
            response.setData(stats);
            response.setMessage("Follow stats retrieved successfully");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);    
            
        } catch (Exception e) {
            log.error("❌ Error getting follow stats: {}", e);
            MobileResponse<FollowStatsDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove a follower
     */
    @PostMapping("/remove-follower")
    public ResponseEntity<MobileResponse<UnfollowUserResponseDto>> removeFollower(
            // @AuthenticationPrincipal OAuth2User currentUser,
            @RequestParam Long currentUserId,
            @RequestBody UnfollowUserRequestDto request) {
        
        try {
            // Long currentUserId = currentUserService.getCurrentUserId(currentUser);
            log.info("👤 User {} removing follower {}", currentUserId, request.getUserIdToUnfollow());
            
            UnfollowUserResponseDto serviceResponse = followService.removeFollower(currentUserId, request);

            MobileResponse<UnfollowUserResponseDto> response = new MobileResponse<>();
            response.setData(serviceResponse);
            response.setMessage("Remove follower successfully");
            response.setStatus(Boolean.TRUE);

            return new ResponseEntity<>(response, HttpStatus.OK);      
            
        } catch (Exception e) {
            log.error("❌ Error removing follower: {}", e);
            MobileResponse<UnfollowUserResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Internal server error ");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
