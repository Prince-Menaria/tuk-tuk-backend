package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor  
public class LeaveRoomRequestDto {
    
    // कौन से room से leave करना है
    // @NotNull(message = "Room ID is required")
    private Long roomId;
    
    // कौन सा user leave कर रहा है  
    // @NotNull(message = "User ID is required")
    private Long userId;
    
    // Leave करने का reason - analytics के लिए
    private String leaveReason; // "VOLUNTARY", "KICKED", "NETWORK_ISSUE", "APP_CLOSE"
    
    // Session feedback - optional
    private Integer sessionRating; // 1-5 stars
    
    private String sessionFeedback; // Text feedback
    
    // Time spent in session (client calculated)
    private Long clientSessionTime; // milliseconds
}
