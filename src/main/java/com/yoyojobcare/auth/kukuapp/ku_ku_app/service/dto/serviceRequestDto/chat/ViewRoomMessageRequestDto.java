package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat;

import lombok.Data;

@Data
public class ViewRoomMessageRequestDto {

    private Long roomId;
    private int page;
    private int size;

}
