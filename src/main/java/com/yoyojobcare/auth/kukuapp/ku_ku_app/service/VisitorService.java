package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.GetVisitorsRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser.RecordVisitRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.RecordVisitResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.VisitorStatsDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser.VisitorsListResponseDto;

public interface VisitorService {

    // Record visit
    RecordVisitResponseDto recordVisit(Long visitorId, RecordVisitRequestDto request);

    // Get visitors
    VisitorsListResponseDto getVisitors(Long profileOwnerId, GetVisitorsRequestDto request);

    // Get visitor stats
    VisitorStatsDto getVisitorStats(Long profileOwnerId);

    // Check if user has visited profile
    boolean hasVisitedProfile(Long visitorId, Long profileOwnerId);

}
