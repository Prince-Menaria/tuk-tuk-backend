package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.followUser;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorsListResponseDto {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalVisitors;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private List<UserVisitorInfoDto> visitors;
    private VisitorStatsDto stats;
}
