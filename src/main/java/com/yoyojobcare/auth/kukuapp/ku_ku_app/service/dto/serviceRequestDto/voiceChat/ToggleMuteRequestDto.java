package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ============ Voice Control Request DTOs ============

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleMuteRequestDto {
    
    // @NotNull(message = "Room ID is required")
    private Long roomId;
    
    // @NotNull(message = "User ID is required")
    private Long userId;
    
    // @NotNull(message = "Mute status is required")
    private Boolean isMuted; // true = mute, false = unmute
    
    // Self mute या host mute
    private Boolean isSelfMute = true; // true = user muting themselves, false = host muting
    
    // Mute duration (for temporary mutes) - minutes
    private Integer muteDurationMinutes;
    
    // Reason for muting - optional
    private String muteReason;
}
