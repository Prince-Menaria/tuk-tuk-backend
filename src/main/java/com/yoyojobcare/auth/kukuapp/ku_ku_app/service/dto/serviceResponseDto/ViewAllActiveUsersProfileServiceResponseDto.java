package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto;

import lombok.Data;

@Data
public class ViewAllActiveUsersProfileServiceResponseDto {

    private Long userId;
    private String fullName;
    private Boolean isFollowing;
    private String profileImage;

}
