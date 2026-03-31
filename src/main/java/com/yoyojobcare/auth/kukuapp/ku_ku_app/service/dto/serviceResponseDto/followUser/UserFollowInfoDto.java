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
public class UserFollowInfoDto {
    private Long followId;
    private UserBasicInfoDto user;
    private String followStatus;
    private LocalDateTime followedAt;
    private String timeAgo;
    private Boolean isFollowingBack; // For followers list - if profile owner follows them back
    private Boolean isVerified;
    private String lastSeen;
}
