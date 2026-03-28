package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation for getters, setters, toString, equals, hashCode
@NoArgsConstructor // Default constructor
@AllArgsConstructor
public class CreateRoomRequestDto {

    // @NotNull(message = "Host ID is required") 
    // Validation - null नहीं हो सकता
    // Host की ID - जो user room create कर रहा है
    private Long hostId;
    
    // Room का name - required field
    // @NotBlank(message = "Room name is required") 
    // Validation - empty या null नहीं हो सकता
    // @Size(min = 3, max = 100, message = "Room name must be between 3 and 100 characters")
    private String roomName;
    
    // Room की description - optional
    // @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    // Room type - PUBLIC या PRIVATE
    // @NotBlank(message = "Room type is required")
    private String roomType; // "PUBLIC" या "PRIVATE"
    
    // Room category - MUSIC, FUN, DATING etc.
    // @NotBlank(message = "Room category is required") 
    private String category; // "MUSIC", "FUN", "DATING", "GAMES", "TALK", "EDUCATION"
    
    // Maximum participants allowed - optional, default 12
    // @Min(value = 2, message = "Minimum 2 participants required")
    // @Max(value = 50, message = "Maximum 50 participants allowed")
    private Integer maxParticipants;
    
    // Password for private rooms - optional
    // @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
    private String roomPassword;
    
    // Background music file name - optional
    // @Size(max = 200, message = "Background music name too long")
    private String backgroundMusic;
    
    // Room cover image URL - optional
    // @Size(max = 500, message = "Image URL too long")
    private String roomImage;
    
    // Room language - default Hindi
    private String roomLanguage = "Hindi";
    
    // Room tags for search - comma separated
    // @Size(max = 200, message = "Tags too long")
    private String tags;
    
    // Age restrictions
    // @Min(value = 13, message = "Minimum age should be 13")
    private Integer minAge = 18;
    
    // @Max(value = 100, message = "Maximum age cannot exceed 100")
    private Integer maxAge = 65;
    
    // Welcome message for new users
    // @Size(max = 200, message = "Welcome message too long")
    private String welcomeMessage = "नमस्ते! इस कमरे में आपका स्वागत है।";
    
    // Auto close timer (hours) - optional
    // @Min(value = 1, message = "Auto close time minimum 1 hour")
    // @Max(value = 48, message = "Auto close time maximum 48 hours")
    private Integer autoCloseAfterHours = 24;

    // Helper method to validate request
    // public boolean isValid() {
    //     return hostId != null && 
    //            roomName != null && !roomName.trim().isEmpty() &&
    //            roomType != null && 
    //            category != null;
    // }
}
