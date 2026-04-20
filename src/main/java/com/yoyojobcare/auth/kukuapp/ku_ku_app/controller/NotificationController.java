package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.NotificationService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.notification.NotificationPageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.notification.NotificationPageResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ Notifications fetch — tab filter ke saath
    @GetMapping("/view-all-notifications")
    public ResponseEntity<MobileResponse<NotificationPageResponseDto>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "Moment") String tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            NotificationPageRequestDto serviceRequestDto = new NotificationPageRequestDto();
            serviceRequestDto.setPage(page);
            serviceRequestDto.setSize(size);
            serviceRequestDto.setTab(tab);
            serviceRequestDto.setUserId(userId);
            NotificationPageResponseDto response = this.notificationService.getNotifications(serviceRequestDto);

            return ResponseEntity.ok(MobileResponse.<NotificationPageResponseDto>builder()
                    .status(true).message("Notifications fetched").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<NotificationPageResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Unread count — badge ke liye
    @GetMapping("/unread-count")
    public ResponseEntity<MobileResponse<Long>> getUnreadCount(@RequestParam Long userId) {
        try {
            long count = this.notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(MobileResponse.<Long>builder()
                    .status(true).message("Unread count").data(count).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<Long>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

     // ✅ Single notification read karo
    @PatchMapping("/mark-as-read-notification")
    public ResponseEntity<MobileResponse<String>> markAsRead(
            @RequestParam Long notificationId) {
        try {
            this.notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(MobileResponse.<String>builder()
                    .status(true).message("Marked as read").data("success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<String>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Sab read karo
    @PatchMapping("/mark-all-as-read-notification")
    public ResponseEntity<MobileResponse<String>> markAllAsRead(@RequestParam Long userId) {
        try {
            this.notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(MobileResponse.<String>builder()
                    .status(true).message("All marked as read").data("success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<String>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

}
