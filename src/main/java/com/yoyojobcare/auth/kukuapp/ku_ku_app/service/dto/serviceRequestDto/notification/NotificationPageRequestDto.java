package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.notification;

import lombok.Data;

@Data
public class NotificationPageRequestDto {

    private Long userId; 
    private String tab; 
    private int page; 
    private int size;

}
