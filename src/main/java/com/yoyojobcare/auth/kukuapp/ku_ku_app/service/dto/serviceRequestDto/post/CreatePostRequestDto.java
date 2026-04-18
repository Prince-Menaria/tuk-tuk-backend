package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post;

import lombok.Data;

@Data
public class CreatePostRequestDto {
    private Long userId;
    private String content;
    private String mediaUrl;
    private String mediaType; // TEXT, IMAGE, VIDEO
    private String visibility; // PUBLIC, FRIENDS, PRIVATE
}
