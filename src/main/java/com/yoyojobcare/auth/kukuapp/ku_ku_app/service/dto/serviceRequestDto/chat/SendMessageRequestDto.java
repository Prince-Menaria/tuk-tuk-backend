package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.chat;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {

    private Long roomId;
    private Long currentUserId; // Jo message ko send karne wala user ki id
    private String content;
    private MessageType messageType;

}
