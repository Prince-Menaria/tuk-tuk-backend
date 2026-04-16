package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post;

import lombok.Data;

@Data
public class ViewAllUserCommentsServiceRequestDto {

    private Long postId;
    private Integer page;
    private Integer size;

}
