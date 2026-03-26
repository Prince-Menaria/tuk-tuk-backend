package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ============ Room Management Response DTOs ============

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Builder pattern for easy object creation
public class CreateRoomResponseDto {
    
    // Created room की basic information
    private Long roomId; // Unique room identifier
    private String roomName; // Display name
    private String description; // Room description
    
    // Agora integration details
    private String channelName; // Agora channel name for voice connection
    private String agoraToken; // Access token for voice chat
    private LocalDateTime tokenExpiryTime; // When token expires
    private String agoraAppId; // Agora app ID for client
    
    // Room configuration
    private String roomType; // "PUBLIC" or "PRIVATE"
    private String category; // Room category
    private Integer maxParticipants; // Maximum allowed participants
    private Integer currentParticipants; // Current participant count
    
    // Host information
    private Long hostId; // Host user ID
    private String hostName; // Host display name
    private String hostImage; // Host profile image
    
    // Additional details
    private String roomImage; // Room cover image
    private String backgroundMusic; // Background music file
    private String welcomeMessage; // Welcome message for new users
    private LocalDateTime createdAt; // Room creation time
    
    // Status information
    private Boolean isActive; // Room is active or not
    private Boolean isLocked; // Room is locked or not
    private String status; // "CREATED", "ACTIVE", "CLOSED"
    
    // Success/Error information
    private String message; // Success or error message
    private Boolean success; // Operation successful या नहीं
    
    // Helper method to get formatted response
    public Map<String, Object> toMap() {
        return Map.of(
            "roomId", roomId,
            "channelName", channelName,
            "agoraToken", agoraToken,
            "roomName", roomName,
            "hostName", hostName,
            "maxParticipants", maxParticipants,
            "status", status,
            "message", message
        );
    }
}