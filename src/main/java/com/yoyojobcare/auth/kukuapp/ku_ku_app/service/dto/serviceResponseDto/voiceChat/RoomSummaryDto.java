package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummaryDto {
    private Long roomId;
    private String roomName;
    private String description;
    private String category;
    private String roomType;
    private String roomImage;
    private String hostName;
    private Integer currentParticipants;
    private Integer maxParticipants;
    private Boolean isActive;
    private Boolean isLocked;
    private Boolean canJoin;
    private String language;
    private List<String> tags;
    private LocalDateTime createdAt;
}
