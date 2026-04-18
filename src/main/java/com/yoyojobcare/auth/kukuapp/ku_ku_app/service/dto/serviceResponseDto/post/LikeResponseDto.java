package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    private Long postId;
    private Boolean liked;      // true = liked, false = unliked
    private Integer totalLikes;
    private String message;
}
