package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRoomListRequestDto {
    
    // Pagination parameters
    private int page = 0;
    private int size = 20;
    
    // Filtering parameters
    private String category; // Filter by category
    private String roomType; // "PUBLIC", "PRIVATE", "ALL"
    private String language; // Filter by language
    
    // Search parameters
    private String searchTerm; // Search in room name/description
    private String tags; // Search by tags
    
    // User preferences - personalized results के लिए
    private Long userId; // For personalized recommendations
    private Integer userAge; // Age appropriate rooms
    private String userLocation; // Nearby rooms
    private List<String> userInterests; // Interest-based filtering
    
    // Sorting parameters
    private String sortBy = "POPULARITY"; // "POPULARITY", "RECENT", "NAME", "PARTICIPANTS"
    private String sortDirection = "DESC"; // "ASC", "DESC"
    
    // Additional filters
    private Boolean hasAvailableSeats = true; // Only rooms with available seats
    private Integer minParticipants; // Minimum current participants
    private Integer maxParticipants; // Maximum current participants
    private Boolean isActive = true; // Only active rooms
    
    // Helper method for validation
    public boolean isValidPagination() {
        return page >= 0 && size > 0 && size <= 100;
    }
}
