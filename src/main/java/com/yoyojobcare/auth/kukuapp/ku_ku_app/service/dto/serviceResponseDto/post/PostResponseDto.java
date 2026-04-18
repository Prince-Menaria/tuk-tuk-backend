package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.post;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long postId;
    private Long userId;
    private String userName;
    private String userImage;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private String visibility;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLikedByMe;     // ✅ Current user ne like kiya?
    private LocalDateTime createdAt;
}
