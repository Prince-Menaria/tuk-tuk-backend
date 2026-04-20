package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.DirectMessageService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.directMessage.SendDmRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage.ConversationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage.DmResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/dm")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DirectMessageController {

    private final DirectMessageService dmService;

    // ✅ Message send
    @PostMapping("/send-direct-message")
    public ResponseEntity<MobileResponse<DmResponseDto>> sendMessage(
            @RequestBody SendDmRequestDto request) {
        try {
            DmResponseDto response = dmService.sendMessage(request);
            return ResponseEntity.ok(MobileResponse.<DmResponseDto>builder()
                    .status(true).message("Message sent").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<DmResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Do users ke beech messages
    @GetMapping("/view-all-messages")
    public ResponseEntity<MobileResponse<Page<DmResponseDto>>> getMessages(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        try {
            Page<DmResponseDto> messages = dmService.getMessages(userId1, userId2, page, size);
            return ResponseEntity.ok(MobileResponse.<Page<DmResponseDto>>builder()
                    .status(true).message("Messages fetched").data(messages).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<Page<DmResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ User ki conversations list — ChatPage ke liye
    @GetMapping("/view-all-conversations")
    public ResponseEntity<MobileResponse<List<ConversationResponseDto>>> getConversations(
            @RequestParam Long userId) {
        try {
            List<ConversationResponseDto> conversations = dmService.getConversations(userId);
            return ResponseEntity.ok(MobileResponse.<List<ConversationResponseDto>>builder()
                    .status(true).message("Conversations fetched").data(conversations).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<List<ConversationResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Read karo
    @PostMapping("/mark-as-read")
    public ResponseEntity<MobileResponse<String>> markAsRead(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        try {
            dmService.markAsRead(senderId, receiverId);
            return ResponseEntity.ok(MobileResponse.<String>builder()
                    .status(true).message("Marked as read").data("success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<String>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

}
