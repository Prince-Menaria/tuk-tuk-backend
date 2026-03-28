package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaiseHandRequestDto {
    
    // @NotNull(message = "Room ID is required")
    private Long roomId;
    
    // @NotNull(message = "User ID is required") 
    private Long userId;
    
    // @NotNull(message = "Hand raise status is required")
    private Boolean isHandRaised; // true = raise hand, false = lower hand
    
    // Request message - why user wants to speak
    private String requestMessage; // "मैं कुछ कहना चाहता हूं", "गाना सुनाना है"
    
    // Priority request - urgent या normal
    private String priority = "NORMAL"; // "NORMAL", "URGENT"
}
