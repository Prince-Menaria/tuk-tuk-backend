package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import org.springframework.data.domain.Page;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.SendMessageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.ViewRoomMessageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.chat.MessageResponseDto;

public interface ChatService {

    MessageResponseDto sendMessage(SendMessageRequestDto request);

    Page<MessageResponseDto> getRoomMessages(ViewRoomMessageRequestDto requestDto);

}
