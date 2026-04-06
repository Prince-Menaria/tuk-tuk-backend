package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicInfoDto {
    private Long userId;
    private String fullName;
    private String username;
    private String profileImage;
    private Boolean isVerified;
    private Boolean isOnline;
    private String lastSeen;
    private String bio;
    private Integer level; // If you have user levels
}
