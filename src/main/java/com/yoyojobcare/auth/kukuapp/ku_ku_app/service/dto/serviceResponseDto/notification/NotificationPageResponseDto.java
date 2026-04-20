package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.notification;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPageResponseDto {
    private List<NotificationResponseDto> notifications;
    private long totalUnread;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
}
