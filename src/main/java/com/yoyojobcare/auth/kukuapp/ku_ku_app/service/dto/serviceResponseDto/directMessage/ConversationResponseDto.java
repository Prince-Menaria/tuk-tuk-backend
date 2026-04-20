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
public class ConversationResponseDto {
    private Long conversationId;
    private Long otherUserId;       // ✅ Dusre user ki info
    private String otherUserName;
    private String otherUserImage;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;    // ✅ Current user ke liye unread
}
