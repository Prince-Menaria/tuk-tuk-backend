package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRoomListResponseDto {
    
    // Pagination information
    private Integer currentPage;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
    
    // Room list
    private List<RoomSummaryDto> rooms;
    
    // Filter information used
    private String appliedCategory;
    private String appliedRoomType;
    private String appliedLanguage;
    private String searchTerm;
    
    // Additional data
    private List<String> availableCategories; // All categories
    private List<String> availableLanguages; // All languages
    private Map<String, Integer> categoryCount; // Room count per category
    
    // Trending information
    private List<RoomSummaryDto> trendingRooms; // Top 5 trending
    private List<RoomSummaryDto> recommendedRooms; // Personalized recommendations
    
    // Success information
    private String message;
    private Boolean success;
}
