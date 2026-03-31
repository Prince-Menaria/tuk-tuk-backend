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
public class FollowUserResponseDto {
    private Boolean success;
    private String message;
    private Long followId;
    private LocalDateTime followedAt;
    private UserBasicInfoDto followedUser;
    private String followStatus;
    private Boolean isNewFollow; // true if first time, false if re-follow
}
