package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRoomDetailsResponseDto {
    
    // Complete room information
    private Long roomId;
    private String roomName;
    private String description;
    private String roomType;
    private String category;
    private String roomLanguage;
    private String tags;
    private String hostImage;
    private String hostName;
    
    // Visual elements
    private String roomImage;
    private String backgroundMusic;
    // private String welcomeMessage;
    
    // Capacity and restrictions
    private Integer maxParticipants;
    private Integer currentParticipants;
    // private Integer minAge;
    // private Integer maxAge;
    
    // Host information
    // private UserSummaryDto host;
    // private List<UserSummaryDto> coHosts;
    
    // // Participants information (if requested)
    // private List<ParticipantDetailDto> participants;
    // private List<ParticipantDetailDto> speakers; // On mic seats
    // private List<ParticipantDetailDto> listeners; // In audience
    // private List<ParticipantDetailDto> handRaisedUsers; // Waiting to speak
    
    // // Recent messages (if requested)
    // private List<MessageDetailDto> recentMessages;
    
    // // Room statistics (if requested)
    // private RoomStatsDto statistics;
    
    // Room status
    private Boolean isActive;
    private Boolean isLocked;
    // private Boolean isPasswordProtected;
    // private Boolean canUserJoin; // Based on age, bans, etc.
    // private String joinRestrictionReason; // Why user can't join
    
    // Agora details for joining
    private String agoraChannelName;
    private String agoraAppId;
    
    // Timing information
    private Instant createdAt;
    private LocalDateTime lastActiveAt;
    private LocalDateTime autoCloseAt;
    
    // Success information  
    private String message;
    private Boolean success;
}
