package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.directMessage.SendDmRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage.ConversationResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.directMessage.DmResponseDto;

public interface DirectMessageService {

    DmResponseDto sendMessage(SendDmRequestDto request);

    Page<DmResponseDto> getMessages(Long userId1, Long userId2, int page, int size);

    List<ConversationResponseDto> getConversations(Long userId);

    void markAsRead(Long senderId, Long receiverId);

}
