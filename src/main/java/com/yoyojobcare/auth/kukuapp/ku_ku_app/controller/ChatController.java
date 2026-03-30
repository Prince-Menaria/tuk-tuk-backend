package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.ChatService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.SendMessageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat.ViewRoomMessageRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.chat.MessageResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<MobileResponse<MessageResponseDto>> sendMessage(
            @RequestBody SendMessageRequestDto request) {

        MessageResponseDto responseDto = chatService.sendMessage(request);

        MobileResponse response = new MobileResponse<>();
        response.setData(responseDto);
        response.setMessage("Send message.. ");
        response.setStatus(Boolean.TRUE);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/view-room-messages")
    public ResponseEntity<MobileResponse<Page<MessageResponseDto>>> getRoomMessages(
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        ViewRoomMessageRequestDto requestDto = new ViewRoomMessageRequestDto();
        requestDto.setRoomId(roomId);
        requestDto.setSize(size);
        requestDto.setPage(page);

        Page<MessageResponseDto> roomMessages = chatService.getRoomMessages(requestDto);

        MobileResponse response = new MobileResponse<>();
        response.setData(roomMessages);
        response.setMessage("All messages view successful ..");
        response.setStatus(Boolean.TRUE);

        return ResponseEntity.ok(response);
    }

}
