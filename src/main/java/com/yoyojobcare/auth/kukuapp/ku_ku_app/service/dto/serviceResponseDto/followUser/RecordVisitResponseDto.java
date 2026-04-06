package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordVisitResponseDto {
    private Boolean success;
    private String message;
    private Long visitorId;
    private LocalDateTime visitedAt;
    private Integer totalVisitCount;
    private Boolean isFirstVisit;
}
