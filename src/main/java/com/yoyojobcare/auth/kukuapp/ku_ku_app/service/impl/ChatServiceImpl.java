package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.ChatMessage;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.ChatRoom;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.MessageType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.ChatMessageRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.ChatRoomRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.ChatService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.SendMessageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.ViewRoomMessageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.chat.MessageResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageResponseDto sendMessage(SendMessageRequestDto request) {
        try {
            log.info("Send Chat Message request info : " + request);
            ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            User sender = userRepository.findById(request.getCurrentUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChatMessage message = ChatMessage.builder()
                    .room(room)
                    .sender(sender)
                    .content(request.getContent().trim())
                    .messageType(request.getMessageType())
                    .timestamp(LocalDateTime.now())
                    .isDeleted(false)
                    .build();

            ChatMessage saved = chatMessageRepository.save(message);

            MessageResponseDto messageResponseDto = toDto(saved);
            messageResponseDto.setMessageType(message.getMessageType());

            // ✅ Real-time broadcast to room
            messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId() + "/messages", messageResponseDto);

            return messageResponseDto;
        } catch (Exception e) {
            log.error("Send Message Exception :: ", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponseDto> getRoomMessages(ViewRoomMessageRequestDto requestDto) {
        try {
            Page<MessageResponseDto> response = chatMessageRepository
                    .findByRoomRoomIdAndIsDeletedFalseOrderByTimestampDesc(requestDto.getRoomId(),
                            PageRequest.of(requestDto.getPage(), requestDto.getSize()))
                    .map(this::toDto);
            return response;

        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    private MessageResponseDto toDto(ChatMessage msg) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setMessageId(msg.getMessageId());
        dto.setRoomId(msg.getRoom().getRoomId());
        dto.setSenderId(msg.getSender().getUserId());
        dto.setSenderName(msg.getSender().getFullName());
        dto.setSenderImage(msg.getSender().getImage());
        dto.setContent(msg.getContent());
        dto.setTimestamp(msg.getTimestamp());
        return dto;
    }

}
