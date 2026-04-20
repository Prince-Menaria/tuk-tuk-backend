package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Conversation;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.DirectMessage;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.ConversationRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.DirectMessageRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.DirectMessageService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.directMessage.SendDmRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage.ConversationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage.DmResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository dmRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public DmResponseDto sendMessage(SendDmRequestDto request) {
        log.info("💬 DM: sender={} → receiver={}", request.getSenderId(), request.getReceiverId());
        try {
            User sender = this.userRepository.findById(request.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            User receiver = this.userRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // ✅ Message save karo
            DirectMessage msg = new DirectMessage();
            msg.setSender(sender);
            msg.setReceiver(receiver);
            msg.setContent(request.getContent().trim());
            msg.setMessageType(request.getMessageType() != null ? request.getMessageType() : "TEXT");
            msg.setIsRead(false);
            msg.setIsDeleted(false);
            msg.setSentAt(LocalDateTime.now());

            DirectMessage saved = this.dmRepository.save(msg);

            // ✅ Conversation update/create karo
            this.updateConversation(sender, receiver, request.getContent());

            DmResponseDto dto = this.toDto(saved);

            // ✅ WebSocket se receiver ko real-time deliver karo
            this.messagingTemplate.convertAndSend(
                    "/topic/dm/" + receiver.getUserId(), dto);

            log.info("✅ DM sent: {}", saved.getMessageId());
            return dto;

        } catch (Exception e) {
            log.error("Send DM error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DmResponseDto> getMessages(Long userId1, Long userId2, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return this.dmRepository.findConversationMessages(userId1, userId2, pageable)
                    .map(this::toDto);
        } catch (Exception e) {
            log.error("Get message error: {}", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getConversations(Long userId) {
        try {
            return this.conversationRepository.findUserConversations(userId)
                    .stream()
                    .map(conv -> {
                        // ✅ Current user ke opposite user ki info
                        boolean isUser1 = conv.getUser1().getUserId().equals(userId);
                        User other = isUser1 ? conv.getUser2() : conv.getUser1();
                        int unread = isUser1 ? conv.getUnreadCountUser1() : conv.getUnreadCountUser2();

                        return ConversationResponseDto.builder()
                                .conversationId(conv.getConversationId())
                                .otherUserId(other.getUserId())
                                .otherUserName(other.getFullName())
                                .otherUserImage(other.getImage())
                                .lastMessage(conv.getLastMessage())
                                .lastMessageAt(conv.getLastMessageAt())
                                .unreadCount(unread)
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Get Conversations error: {}", e);
            throw e;
        }
    }

    @Override
    public void markAsRead(Long senderId, Long receiverId) {
        try {
            // ✅ Unread messages read karo
            List<DirectMessage> unread = this.dmRepository
                    .findAll()
                    .stream()
                    .filter(m -> m.getSender().getUserId().equals(senderId)
                            && m.getReceiver().getUserId().equals(receiverId)
                            && !m.getIsRead())
                    .collect(Collectors.toList());

            unread.forEach(m -> m.setIsRead(true));
            this.dmRepository.saveAll(unread);

            // ✅ Conversation unread count reset karo
            this.conversationRepository.findBetweenUsers(senderId, receiverId).ifPresent(conv -> {
                if (conv.getUser1().getUserId().equals(receiverId)) {
                    conv.setUnreadCountUser1(0);
                } else {
                    conv.setUnreadCountUser2(0);
                }
                this.conversationRepository.save(conv);
            });
        } catch (Exception e) {
            log.error("mark As Read error: {}", e);
            throw e;
        }
    }

    // ✅ Conversation update helper
    private void updateConversation(User sender, User receiver, String lastMsg) {
        Conversation conv = conversationRepository
                .findBetweenUsers(sender.getUserId(), receiver.getUserId())
                .orElse(null);

        if (conv == null) {
            // ✅ New conversation banao
            conv = new Conversation();
            conv.setUser1(sender);
            conv.setUser2(receiver);
            conv.setLastMessage(lastMsg);
            conv.setLastMessageAt(LocalDateTime.now());
            conv.setUnreadCountUser1(0);
            conv.setUnreadCountUser2(1); // receiver ko unread

        } else {
            conv.setLastMessage(lastMsg);
            conv.setLastMessageAt(LocalDateTime.now());
            // ✅ Receiver ka unread increment karo
            if (conv.getUser1().getUserId().equals(receiver.getUserId())) {
                conv.setUnreadCountUser1(conv.getUnreadCountUser1() + 1);
            } else {
                conv.setUnreadCountUser2(conv.getUnreadCountUser2() + 1);
            }
        }
        conversationRepository.save(conv);
    }

    private DmResponseDto toDto(DirectMessage msg) {
        return DmResponseDto.builder()
                .messageId(msg.getMessageId())
                .senderId(msg.getSender().getUserId())
                .senderName(msg.getSender().getFullName())
                .senderImage(msg.getSender().getImage())
                .receiverId(msg.getReceiver().getUserId())
                .receiverName(msg.getReceiver().getFullName())
                .content(msg.getContent())
                .messageType(msg.getMessageType())
                .isRead(msg.getIsRead())
                .sentAt(msg.getSentAt())
                .build();
    }

}
