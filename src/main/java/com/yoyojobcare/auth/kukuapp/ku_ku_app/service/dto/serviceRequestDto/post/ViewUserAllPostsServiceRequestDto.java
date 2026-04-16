package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post;

import lombok.Data;

@Data
public class ViewUserAllPostsServiceRequestDto {

    private Long userId;
    private Long currentUserId;
    private Integer page;
    private Integer size;

}
