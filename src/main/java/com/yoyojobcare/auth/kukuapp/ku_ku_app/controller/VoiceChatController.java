package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.VoiceChatService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.CreateRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.GetRoomDetailsRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.GetRoomListRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.JoinRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.JoinRoomSeatRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.LeaveRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.LeaveRoomSeatRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.CreateRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.GetRoomDetailsResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.GetRoomListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.JoinRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.LeaveRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.ParticipantResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logging के लिए
@RestController // REST API controller annotation
@RequiredArgsConstructor // Constructor injection
@RequestMapping("/api/v1/voice-chat") // Base path for all voice chat APIs
@CrossOrigin(origins = "*") // CORS configuration - all origins allowed for now
@Tag(name = "Voice Chat", description = "Complete voice chat room management APIs") // Swagger documentation
public class VoiceChatController {

    private final VoiceChatService voiceChatService; // Service layer injection

    /**
     * नया voice chat room बनाना
     * POST /api/v1/voice-chat/rooms/create
     */
    @Operation(summary = "Create New Voice Chat Room", description = "Creates a new voice chat room with specified configuration. "
            +
            "Host will automatically join the room and get seat 1.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/rooms/create-room")
    public ResponseEntity<MobileResponse<CreateRoomResponseDto>> createRoom(
            @Parameter(description = "Room creation details", required = true) @RequestBody CreateRoomRequestDto request) {

        try {
            // Log incoming request
            log.info("🏗️ Room creation request received from user: {}",
                    request.getHostId() != null ? request.getHostId() : "unknown");

            // Execute room creation business logic
            CreateRoomResponseDto response = voiceChatService.createRoom(request);

            // Wrap response in standard mobile response format
            MobileResponse<CreateRoomResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setData(response);
            mobileResponse.setMessage("Room created successfully..");
            mobileResponse.setStatus(Boolean.TRUE);

            log.info("✅ Room created successfully with ID: {}", response.getRoomId());
            return new ResponseEntity<>(mobileResponse, HttpStatus.OK);

        } catch (Exception e) {
            // Handle unexpected technical errors
            log.error("💥 Unexpected error in room creation: {}", e.getMessage(), e);

            MobileResponse<CreateRoomResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Room not create successful...");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Join Voice Chat Room", description = "Join an existing voice chat room. User will be added as listener initially.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully joined room"),
            @ApiResponse(responseCode = "400", description = "Cannot join room - room full, wrong password, etc."),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "403", description = "User banned or access denied")
    })
    @PostMapping("/rooms/join")
    public ResponseEntity<MobileResponse<JoinRoomResponseDto>> joinRoom(
            @RequestBody JoinRoomRequestDto request) {

        try {
            log.info("🚪 User {} attempting to join room {}", request.getRoomId());

            // Execute join room business logic
            JoinRoomResponseDto response = voiceChatService.joinRoom(request);

            // Success response
            MobileResponse<JoinRoomResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setData(response);
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setStatus(Boolean.TRUE);

            log.info("✅ User {} successfully joined room {}", request.getRoomId());
            return new ResponseEntity<>(mobileResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("💥 Unexpected error joining room: {}", e.getMessage(), e);

            MobileResponse<JoinRoomResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Room join successful");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Voice chat room से leave करना
     * POST /api/v1/voice-chat/rooms/leave
     */
    @Operation(summary = "Leave Voice Chat Room", description = "Leave current voice chat room. Session statistics will be calculated.")
    @PostMapping("/rooms/leave")
    public ResponseEntity<MobileResponse<LeaveRoomResponseDto>> leaveRoom(
            @RequestBody LeaveRoomRequestDto request) {

        try {
            log.info("🚪 User {} leaving room {}", request.getRoomId());

            LeaveRoomResponseDto response = voiceChatService.leaveRoom(request);

            MobileResponse<LeaveRoomResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setData(response);
            mobileResponse.setMessage(response.getMessage());
            mobileResponse.setStatus(Boolean.TRUE);

            log.info("✅ User {} left room {} after {} seconds",
                    request.getRoomId(), response.getSessionDurationSeconds());

            return new ResponseEntity<>(mobileResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("💥 Unexpected error leaving room: {}", e.getMessage(), e);

            MobileResponse<LeaveRoomResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Error occurred while leaving room");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Available rooms की list get करना
     * GET /api/v1/voice-chat/rooms/list
     */
    @Operation(summary = "Get Available Rooms List", description = "Get paginated list of available voice chat rooms with filtering options")
    @GetMapping("/rooms/list")
    public ResponseEntity<MobileResponse<GetRoomListResponseDto>> getRoomList(

            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (max 50)") @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,

            @Parameter(description = "Filter by room type (PUBLIC/PRIVATE/ALL)") @RequestParam(defaultValue = "ALL", required = false) String roomType,

            @Parameter(description = "Filter by language") @RequestParam(required = false) String language,

            @Parameter(description = "Search in room name/description") @RequestParam(required = false) String search,

            @Parameter(description = "Sort by (POPULARITY/RECENT/NAME)") @RequestParam(defaultValue = "POPULARITY", required = false) String sortBy) {

        try {
            log.info("📋 Room list requested by user: {} with filters - category: {}, type: {}, search: {}",
                    category, roomType, search);

            // Build request DTO
            GetRoomListRequestDto request = new GetRoomListRequestDto();
            request.setPage(page);
            request.setSize(Math.min(size, 50)); // Maximum 50 items per page
            request.setCategory(category);
            request.setRoomType(roomType);
            request.setLanguage(language);
            request.setSearchTerm(search);
            request.setSortBy(sortBy);

            GetRoomListResponseDto response = voiceChatService.getRoomList(request);

            MobileResponse<GetRoomListResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setData(response);
            mobileResponse.setMessage("Rooms retrieved successfully");
            mobileResponse.setStatus(Boolean.TRUE);

            log.info("✅ Returned {} rooms out of {} total",
                    response.getRooms().size(), response.getTotalElements());

            return new ResponseEntity<>(mobileResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("💥 Error retrieving room list: {}", e.getMessage(), e);

            MobileResponse<GetRoomListResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Error occurred while fetching rooms");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Specific room की detailed information get करना
     * GET /api/v1/voice-chat/rooms/{roomId}/details
     */
    @Operation(summary = "Get Room Details", description = "Get detailed information about a specific room including participants and recent messages")
    @GetMapping("/rooms/{roomId}/details")
    public ResponseEntity<MobileResponse<GetRoomDetailsResponseDto>> getRoomDetails(

            @Parameter(description = "Room ID", required = true) @RequestParam Long roomId,

            @Parameter(description = "Include participant list") @RequestParam(defaultValue = "true") boolean includeParticipants,

            @Parameter(description = "Include recent messages") @RequestParam(defaultValue = "true") boolean includeMessages,

            @Parameter(description = "Number of recent messages to include") @RequestParam(defaultValue = "10") int messageCount) {

        try {

            log.info("🔍 Room details requested for room {} by user {}", roomId);

            GetRoomDetailsRequestDto request = new GetRoomDetailsRequestDto();
            request.setRoomId(roomId);
            // request.setUserId(getCurrentUserId(currentUser)); // Get from service
            request.setIncludeParticipants(includeParticipants);
            request.setIncludeRecentMessages(includeMessages);
            request.setMessageCount(messageCount);

            GetRoomDetailsResponseDto response = voiceChatService.getRoomDetails(request);

            MobileResponse<GetRoomDetailsResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setData(response);
            mobileResponse.setMessage("Room details retrieved successfully");
            mobileResponse.setStatus(true);

            return new ResponseEntity<>(mobileResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("💥 Unexpected error getting room details: {}", e.getMessage(), e);

            MobileResponse<GetRoomDetailsResponseDto> errorResponse = new MobileResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Error occurred while fetching room details");
            errorResponse.setStatus(false);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Room ke active participants fetch karo
    @GetMapping("/rooms/{roomId}/participants")
    public ResponseEntity<MobileResponse<List<ParticipantResponseDto>>> getRoomParticipants(
            @PathVariable Long roomId) {
        try {
            List<ParticipantResponseDto> participants = voiceChatService.getRoomParticipants(roomId);

            MobileResponse<List<ParticipantResponseDto>> response = new MobileResponse<>();
            response.setData(participants);
            response.setMessage("Participants fetched successfully");
            response.setStatus(true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get participants error: {}", e.getMessage(), e);
            MobileResponse<List<ParticipantResponseDto>> error = new MobileResponse<>();
            error.setStatus(false);
            error.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ Seat join karo
    @PostMapping("/rooms/join-seat")
    public ResponseEntity<MobileResponse<ParticipantResponseDto>> joinSeat(
            @RequestParam Long roomId,
            @RequestParam Long userId,
            @RequestParam Integer seatNumber) {
        try {
            JoinRoomSeatRequestDto requestDto = new JoinRoomSeatRequestDto();
            requestDto.setRoomId(roomId);
            requestDto.setUserId(userId);
            requestDto.setSeatNumber(seatNumber);
            ParticipantResponseDto response = voiceChatService.joinSeat(requestDto);
            MobileResponse<ParticipantResponseDto> mobileResponse = new MobileResponse<>();
            mobileResponse.setData(response);
            mobileResponse.setMessage("Seat joined successfully");
            mobileResponse.setStatus(true);
            return ResponseEntity.ok(mobileResponse);
        } catch (Exception e) {
            log.error("Join seat error: {}", e.getMessage(), e);
            MobileResponse<ParticipantResponseDto> error = new MobileResponse<>();
            error.setStatus(false);
            error.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ Seat leave karo
    @PostMapping("/rooms/leave-seat")
    public ResponseEntity<MobileResponse<String>> leaveSeat(
            @RequestParam Long roomId,
            @RequestParam Long userId) {
        try {
            LeaveRoomSeatRequestDto requestDto = new LeaveRoomSeatRequestDto();
            requestDto.setRoomId(roomId);
            requestDto.setUserId(userId);

            voiceChatService.leaveSeat(requestDto);
            MobileResponse<String> mobileResponse = new MobileResponse<>();
            mobileResponse.setData("Seat left");
            mobileResponse.setMessage("Seat left successfully");
            mobileResponse.setStatus(true);
            return ResponseEntity.ok(mobileResponse);
        } catch (Exception e) {
            log.error("Leave seat error: {}", e.getMessage(), e);
            MobileResponse<String> error = new MobileResponse<>();
            error.setStatus(false);
            error.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

}
