package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import lombok.Data;

@Data
public class ParticipantResponseDto {
    private Long participantId;
    private Long userId;
    private String fullName;
    private String userImage;
    private String role; // HOST, SPEAKER, LISTENER
    private String status; // ACTIVE
    private Integer seatNumber;
    private Boolean isMuted;
    private Boolean isAudioEnabled;
    private String agoraUid;
}
