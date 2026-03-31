package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVisitorInfoDto {
    private Long visitorId;
    private UserBasicInfoDto visitor;
    private String visitorType;
    private LocalDateTime lastVisitAt;
    private LocalDateTime firstVisitAt;
    private Integer visitCount;
    private String timeAgo;
    private String sourcePage;
    
    // Additional info
    private Boolean isFollowing; // If the profile owner follows this visitor
    private Boolean isFollower; // If this visitor follows the profile owner
    private Boolean isMutualFollow;
    private Boolean isVerified;
}
