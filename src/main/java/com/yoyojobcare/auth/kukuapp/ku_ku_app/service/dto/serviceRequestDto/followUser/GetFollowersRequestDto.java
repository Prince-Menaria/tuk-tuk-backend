package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.followUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFollowersRequestDto {
    private int page = 0;
    private int size = 20;
    private String sortBy = "followedAt";
    private String sortDirection = "DESC";
    private String status = "ACTIVE"; // ACTIVE, ALL
}
