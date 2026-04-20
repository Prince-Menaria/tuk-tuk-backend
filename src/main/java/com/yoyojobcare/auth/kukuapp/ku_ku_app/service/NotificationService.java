package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.notification.NotificationPageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.notification.NotificationPageResponseDto;

public interface NotificationService {
    // ✅ Create
    void createNotification(User receiver, User sender, String type,
                            String message, Long referenceId, String referenceType);

    // ✅ Fetch
    NotificationPageResponseDto getNotifications(NotificationPageRequestDto requestDto);
    long getUnreadCount(Long userId);

    // ✅ Read
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
