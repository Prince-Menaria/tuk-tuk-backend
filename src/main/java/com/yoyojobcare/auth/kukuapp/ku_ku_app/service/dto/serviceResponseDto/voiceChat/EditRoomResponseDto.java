package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditRoomResponseDto {
    private Long roomId;
    private String roomName;
    private String description;
    private String roomType;
    private String category;
    private Integer maxParticipants;
    private String backgroundMusic;
    private String roomImage;
    private Boolean success;
    private String message;
}
