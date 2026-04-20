package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Notification;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.NotificationRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.NotificationService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.notification.NotificationPageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.notification.NotificationPageResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.notification.NotificationResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void createNotification(User receiver, User sender, String type,
            String message, Long referenceId, String referenceType) {
        try {
            // ✅ Apne aap ko notification mat do
            if (sender != null && sender.getUserId().equals(receiver.getUserId()))
                return;

            Notification notification = new Notification();
            notification.setReceiver(receiver);
            notification.setSender(sender);
            notification.setType(type);
            notification.setMessage(message);
            notification.setReferenceId(referenceId);
            notification.setReferenceType(referenceType);
            notification.setIsRead(false);
            notification.setNotifiedAt(LocalDateTime.now());

            Notification saved = this.notificationRepository.save(notification);
            log.info("🔔 Notification created: type={}, receiver={}", type, receiver.getUserId());

            // ✅ Real-time WebSocket push
            NotificationResponseDto dto = this.toDto(saved);
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + receiver.getUserId(), dto);

        } catch (Exception e) {
            log.error("Create notification error: {}", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPageResponseDto getNotifications(NotificationPageRequestDto requestDto) {
        try {
            Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize());

            // ✅ Tab ke hisaab se filter
            // "Moment" = POST_LIKE, POST_COMMENT, COMMENT_REPLY, FOLLOW
            // "Room" = ROOM_GIFT, ROOM_JOIN, ROOM_INVITE
            List<String> types = new LinkedList<>();
            if ("Room".equalsIgnoreCase(requestDto.getTab())) {
                types = List.of("ROOM_GIFT", "ROOM_JOIN", "ROOM_INVITE");
            } else {
                types = List.of("POST_LIKE", "POST_COMMENT", "COMMENT_REPLY", "FOLLOW", "SYSTEM");
            }

            Page<Notification> notifPage = this.notificationRepository
                    .findByReceiverUserIdAndTypeInOrderByNotifiedAtDesc(requestDto.getUserId(), types, pageable);

            List<NotificationResponseDto> dtos = notifPage.getContent()
                    .stream().map(this::toDto).collect(Collectors.toList());

            long unreadCount = this.notificationRepository.countByReceiverUserIdAndIsReadFalse(requestDto.getUserId());

            return NotificationPageResponseDto.builder()
                    .notifications(dtos)
                    .totalUnread(unreadCount)
                    .currentPage(requestDto.getPage())
                    .totalPages(notifPage.getTotalPages())
                    .hasNext(notifPage.hasNext())
                    .build();
        } catch (Exception e) {
            log.error("get Notifications error: {}", e);
            throw e;
        }
    }

    @Override
    public long getUnreadCount(Long userId) {
        try {
            return this.notificationRepository.countByReceiverUserIdAndIsReadFalse(userId);

        } catch (Exception e) {
            log.error("get Unread Count error: {}", e);
            throw e;
        }
    }

    @Override
    public void markAsRead(Long notificationId) {
        try {
            this.notificationRepository.findById(notificationId).ifPresent(n -> {
                n.setIsRead(true);
                notificationRepository.save(n);
            });
        } catch (Exception e) {
            log.error("mark As Read error: {}", e);
            throw e;
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        try {
            this.notificationRepository.markAllAsRead(userId);
            log.info("✅ All notifications marked read for user: {}", userId);
        } catch (Exception e) {
            log.error("mark All As Read error: {}", e);
            throw e;
        }
    }

    // ✅ Helper — "22 hours ago" format
    private String formatTimeAgo(LocalDateTime time) {
        if (time == null)
            return "";
        long seconds = java.time.Duration.between(time, LocalDateTime.now()).getSeconds();
        if (seconds < 60)
            return seconds + " seconds ago";
        if (seconds < 3600)
            return (seconds / 60) + " minutes ago";
        if (seconds < 86400)
            return (seconds / 3600) + " hours ago";
        if (seconds < 604800)
            return (seconds / 86400) + " days ago";
        return (seconds / 604800) + " weeks ago";
    }

    private NotificationResponseDto toDto(Notification n) {
        return NotificationResponseDto.builder()
                .notificationId(n.getNotificationId())
                .senderId(n.getSender() != null ? n.getSender().getUserId() : null)
                .senderName(n.getSender() != null ? n.getSender().getFullName() : "System")
                .senderImage(n.getSender() != null ? n.getSender().getImage() : null)
                .type(n.getType())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .referenceType(n.getReferenceType())
                .isRead(n.getIsRead())
                .timeAgo(formatTimeAgo(n.getNotifiedAt()))
                .notifiedAt(n.getNotifiedAt())
                .build();
    }

}
