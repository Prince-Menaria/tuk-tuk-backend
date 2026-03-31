package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnfollowUserRequestDto {
    
    private Long userIdToUnfollow;
}
