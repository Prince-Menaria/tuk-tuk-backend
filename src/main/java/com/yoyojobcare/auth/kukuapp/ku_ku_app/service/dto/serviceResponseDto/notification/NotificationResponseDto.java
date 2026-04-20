package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.notification;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long notificationId;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private String type;
    private String message;
    private Long referenceId;
    private String referenceType;
    private Boolean isRead;
    private String timeAgo;         // "22 hours ago"
    private LocalDateTime notifiedAt;
}
