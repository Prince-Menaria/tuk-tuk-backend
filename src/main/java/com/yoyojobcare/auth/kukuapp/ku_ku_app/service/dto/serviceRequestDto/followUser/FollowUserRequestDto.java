package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowUserRequestDto {
    
    private Long userIdToFollow;
    private String source; // Optional: where the follow action originated
}
