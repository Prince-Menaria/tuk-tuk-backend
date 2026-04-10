package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import lombok.Data;

@Data
public class JoinRoomSeatRequestDto {

    private Long roomId;
    private Long userId;
    private Integer seatNumber;

}
