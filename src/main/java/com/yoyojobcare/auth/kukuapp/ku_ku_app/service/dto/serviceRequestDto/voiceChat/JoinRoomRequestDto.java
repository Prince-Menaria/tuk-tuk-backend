package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  
@AllArgsConstructor
public class JoinRoomRequestDto {

    // कौन से room में join करना है
    // @NotNull(message = "Room ID is required")
    private Long roomId;
    
    // कौन सा user join कर रहा है
    // @NotNull(message = "User ID is required")
    private Long userId;
    
    // // Private room के लिए password - optional
    // private String password;
    
    // // Join करने का reason - optional, analytics के लिए
    // private String joinReason; // "SEARCH", "INVITE", "RECOMMENDATION", "DIRECT_LINK"
    
    // // Client information - debugging के लिए
    // private String clientInfo; // "Android 1.2.3", "iOS 2.1.0"
    
    // // User की current location - nearby rooms के लिए (optional)
    // private String userLocation;
    
    // // Helper method
    // public boolean isValidForPrivateRoom() {
    //     return password != null && !password.trim().isEmpty();
    // }

}
