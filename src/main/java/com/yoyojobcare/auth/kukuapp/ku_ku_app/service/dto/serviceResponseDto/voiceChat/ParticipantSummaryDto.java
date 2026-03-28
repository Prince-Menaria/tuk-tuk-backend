package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ============ Supporting DTOs ============

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantSummaryDto {
    private Long userId;
    private String userName;
    private String userImage;
    private String role; // "HOST", "CO_HOST", "SPEAKER", "LISTENER"
    private Integer seatNumber; // null for audience
    private Boolean isMuted;
    private Boolean isHandRaised;
    private LocalDateTime joinedAt;
    private Integer level; // User's app level
    private List<String> badges; // User's badges
}
