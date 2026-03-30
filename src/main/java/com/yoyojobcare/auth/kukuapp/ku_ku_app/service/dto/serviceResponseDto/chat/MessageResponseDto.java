package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.chat;

import java.time.LocalDateTime;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.MessageType;

import lombok.Builder;
import lombok.Data;

@Data
public class MessageResponseDto {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;

}
