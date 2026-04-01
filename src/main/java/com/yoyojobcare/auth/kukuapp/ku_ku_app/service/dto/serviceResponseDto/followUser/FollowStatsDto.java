package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowStatsDto {
    private Long totalFollowers;
    private Long totalFollowing;
    private Long mutualFollows;
    private Long recentFollowersCount; // Last 7 days
    private Boolean isPrivateAccount;
    private Boolean isFollowRequestPending;
}
