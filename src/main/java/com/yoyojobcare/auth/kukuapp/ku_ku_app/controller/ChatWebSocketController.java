package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.ChatService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.SendMessageRequestDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat/send") // client sends to /app/chat/send
    public void sendMessage(@Payload SendMessageRequestDto request) {
        // NOTE: WebSocket auth के लिए Principal setup चाहिए
        chatService.sendMessage(request);
    }

}
