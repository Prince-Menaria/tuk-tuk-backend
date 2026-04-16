package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.post;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String userName;
    private String userImage;
    private String content;
    private Integer likeCount;
    private Long parentCommentId;
    private List<CommentResponseDto> replies;
    private LocalDateTime commentedAt;
}
