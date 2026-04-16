package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post;

import lombok.Data;

@Data
public class CommentRequestDto {
    private Long postId;
    private Long userId;
    private String content;
    private Long parentCommentId; // null = top level, value = reply
}
