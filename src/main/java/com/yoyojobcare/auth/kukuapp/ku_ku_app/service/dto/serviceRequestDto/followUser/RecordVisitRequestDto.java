package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordVisitRequestDto {
    private Long profileOwnerId;
    private String visitorType; // PROFILE_VIEW, ROOM_VISIT, etc.
    private String sourcePage; // Optional context
    private String deviceInfo; // Optional device information

}
