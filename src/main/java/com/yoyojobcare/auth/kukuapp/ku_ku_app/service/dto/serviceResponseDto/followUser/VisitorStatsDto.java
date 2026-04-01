package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitorStatsDto {
    private Long totalUniqueVisitors;
    private Long todayVisitors;
    private Long weekVisitors;
    private Long monthVisitors;
    private Long totalProfileViews; // Sum of all visit counts
    private UserVisitorInfoDto topVisitor; // Most frequent visitor
    private String peakVisitTime; // Most active time
}
