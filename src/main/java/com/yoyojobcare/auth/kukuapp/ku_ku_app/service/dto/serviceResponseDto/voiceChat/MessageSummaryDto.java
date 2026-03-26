package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSummaryDto {
    private Long messageId;
    private String senderName;
    private String content;
    private String messageType; // "TEXT", "EMOJI", "GIFT", etc.
    private LocalDateTime timestamp;
    private Boolean isPinned;
}
