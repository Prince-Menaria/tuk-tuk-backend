package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.Data;

@Data
public class EditRoomRequestDto {

    private Long roomId;

    private String roomName;
    
    private String description;
    
    private String roomType; // "PUBLIC" या "PRIVATE"
  
    private String category; // "MUSIC", "FUN", "DATING", "GAMES", "TALK", "EDUCATION"
    
    private Integer maxParticipants;
      
    // Background music file name - optional
    // @Size(max = 200, message = "Background music name too long")
    private String backgroundMusic;
    
    private String roomImage;


}
