package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmResponseDto {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private Long receiverId;
    private String receiverName;
    private String content;
    private String messageType;
    private Boolean isRead;
    private LocalDateTime sentAt;
}
