package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialSummaryDto {
    private Long userId;
    private String userName;
    private FollowStatsDto followStats;
    private VisitorStatsDto visitorStats;
    private List<UserBasicInfoDto> recentFollowers; // Last 5
    private List<UserBasicInfoDto> recentVisitors; // Last 5
    private List<UserBasicInfoDto> suggestedFollows; // Recommendations
}
