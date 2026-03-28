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
public class LeaveRoomResponseDto {
    
    private Long roomId;
    private Long userId;
    private String userName;
    
    // Session summary
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Long sessionDurationSeconds; // Total time spent
    private Integer messagesPosted; // Messages posted in session
    private Integer giftsReceived; // Gifts received
    private Integer giftsGiven; // Gifts given
    
    // Leave details
    private String leaveReason; // Why user left
    private Boolean wasKicked; // Was forcefully removed
    private Boolean wasBanned; // Was banned from room
    
    // Room status after user left
    private Integer remainingParticipants;
    private Boolean roomStillActive;
    private String newHostName; // If host transfer happened
    
    // User feedback (optional)
    private Integer sessionRating; // 1-5 stars
    private String sessionFeedback;
    
    // Recommendations for user
    private List<RoomSummaryDto> recommendedRooms;
    
    // Success information
    private String message;
    private Boolean success;
    
    // Helper method
    public String getFormattedSessionDuration() {
        if (sessionDurationSeconds == null) return "0 minutes";
        
        long minutes = sessionDurationSeconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return hours + " hours " + (minutes % 60) + " minutes";
        } else {
            return minutes + " minutes";
        }
    }
}
