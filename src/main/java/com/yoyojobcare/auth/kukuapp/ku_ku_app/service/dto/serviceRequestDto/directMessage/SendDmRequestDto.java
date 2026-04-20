package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.directMessage;

import lombok.Data;

@Data
public class SendDmRequestDto {
    private Long senderId;
    private Long receiverId;
    private String content;
    private String messageType; // TEXT, IMAGE
}
