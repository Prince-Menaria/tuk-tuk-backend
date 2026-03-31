package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetVisitorsRequestDto {
    private int page = 0;
    private int size = 20;
    private String visitorType; // Filter by visitor type
    private String timeFilter; // TODAY, WEEK, MONTH, ALL
    private String sortBy = "lastVisitAt";
    private String sortDirection = "DESC";
}
