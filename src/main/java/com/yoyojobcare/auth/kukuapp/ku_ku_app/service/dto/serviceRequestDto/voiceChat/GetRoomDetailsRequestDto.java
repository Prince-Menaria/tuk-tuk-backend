package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRoomDetailsRequestDto {
    
    // कौन से room की details चाहिए
    // @NotNull(message = "Room ID is required")
    private Long roomId;
    
    // कौन सा user request कर रहा है - permission check के लिए
    // @NotNull(message = "User ID is required") 
    private Long userId;
    
    // Include participants list या नहीं
    private Boolean includeParticipants = true;
    
    // Include recent messages या नहीं
    private Boolean includeRecentMessages = true;
    
    // How many recent messages to include
    private Integer messageCount = 20;
    
    // Include room statistics या नहीं
    private Boolean includeStats = false;
}
