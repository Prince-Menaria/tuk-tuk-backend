package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.SocialSummaryDto;

public interface SocialService {

    // Get combined social summary (followers, following, visitors)
    SocialSummaryDto getSocialSummary(Long userId);

    // Get suggested users to follow
    // List<UserBasicInfoDto> getSuggestedFollows(Long userId, int limit);

}
