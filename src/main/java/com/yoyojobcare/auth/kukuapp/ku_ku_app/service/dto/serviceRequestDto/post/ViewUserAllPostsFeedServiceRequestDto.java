package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post;

import lombok.Data;

@Data
public class ViewUserAllPostsFeedServiceRequestDto {

    private Long currentUserId;
    private Integer page;
    private Integer size;

}
