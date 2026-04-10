package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRoomResponseDto {
    
    // Room information
    private Long roomId;
    private String roomName;
    private String roomImage;
    private String description;
    private String category;
    private String roomType;
    
    // Voice chat connection details
    private String channelName; // Agora channel for connection
    private String agoraToken; // User-specific token
    private LocalDateTime tokenExpiryTime;
    private String agoraAppId;
    private String agoraUid; // User's Agora UID
    
    // Participant information
    private Long participantId; // Participant record ID
    private String role; // "HOST", "CO_HOST", "SPEAKER", "LISTENER"
    private String status; // "ACTIVE", "MUTED", etc.
    private Integer seatNumber; // Mic seat number (null for audience)
    private Boolean canSpeak; // Speaking permission
    private Boolean isMuted; // Mute status
    
    // Room status
    private Integer currentParticipants;
    private Integer maxParticipants;
    private Boolean isRoomFull;
    private Boolean isRoomLocked;
    
    // Host information
    private Long hostId;
    private String hostName;
    private String hostImage;
    
    // Additional settings
    private String roomLanguage;
    private String backgroundMusic;
    private String welcomeMessage;
    
    // Participant list (basic info)
    private List<ParticipantSummaryDto> otherParticipants;
    
    // Recent messages (last 10)
    private List<MessageSummaryDto> recentMessages;
    
    // Room rules and guidelines
    private List<String> roomRules;
    
    // Success information
    private String message;
    private Boolean success;
    private LocalDateTime joinedAt;
    
    // Helper methods
    public boolean isHost() {
        return "HOST".equals(this.role);
    }
    
    public boolean canManageRoom() {
        return "HOST".equals(this.role) || "CO_HOST".equals(this.role);
    }
}
