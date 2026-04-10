package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.config.AgoraConfig;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.ChatRoom;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RoomParticipant;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.ParticipantRole;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.ParticipantStatus;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.RoomCategory;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.RoomType;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.ChatMessageRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.ChatRoomRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RoomParticipantRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.AgoraTokenService;
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
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.RoomSummaryDto;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoiceChatServiceImpl implements VoiceChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository messageRepository;
    private final AgoraTokenService agoraTokenService; // ✅ inject karo
    private final AgoraConfig agoraConfig;

    @Override
    public CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto) {
        try {
            log.info("🏗️ Creating room for user: {} with name: {}",
                    requestDto.getHostId(), requestDto.getRoomName());

            User hostUser = userRepository.findById(requestDto.getHostId())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "Host user not found with User ID: " + requestDto.getHostId()));

            ChatRoom room = new ChatRoom();
            room.setRoomName(requestDto.getRoomName());
            room.setRoomImage(requestDto.getRoomImage());
            room.setRoomPassword(requestDto.getRoomPassword());
            room.setDescription(requestDto.getDescription());
            room.setHostId(hostUser.getUserId());
            room.setHostName(hostUser.getFullName());
            room.setMaxParticipants(requestDto.getMaxParticipants() != null ? requestDto.getMaxParticipants() : 12);
            room.setCurrentParticipants(0);

            try {
                room.setRoomType(RoomType.valueOf(requestDto.getRoomType().toUpperCase()));
                room.setCategory(RoomCategory.valueOf(requestDto.getCategory().toUpperCase()));
            } catch (Exception e) {
                log.error("Invalid room type or category: {}", e.getMessage());
                room.setRoomType(RoomType.PUBLIC);
                room.setCategory(RoomCategory.FUN);
            }

            // ✅ Agora channel name generate karo — save karo room mein
            String channelName = agoraTokenService.generateChannelName(System.currentTimeMillis());
            room.setAgoraChannelName(channelName);

            ChatRoom savedRoom = chatRoomRepository.save(room);
            log.info("✅ Room saved with ID: {}", savedRoom.getRoomId());
            // ✅ Host ke liye token generate karo
            String agoraToken = agoraTokenService.generateToken(channelName, hostUser.getUserId());

            CreateRoomResponseDto rs = new CreateRoomResponseDto();
            rs.setRoomId(savedRoom.getRoomId());
            rs.setRoomImage(savedRoom.getRoomImage());
            rs.setRoomName(savedRoom.getRoomName());
            rs.setRoomType(savedRoom.getRoomType().name());
            rs.setDescription(savedRoom.getDescription());
            rs.setHostId(savedRoom.getHostId());
            rs.setHostName(savedRoom.getHostName());
            rs.setHostImage(hostUser.getImage());
            rs.setMaxParticipants(savedRoom.getMaxParticipants());
            rs.setCurrentParticipants(savedRoom.getCurrentParticipants());
            rs.setChannelName(channelName); // ✅ Frontend ko chahiye
            rs.setAgoraToken(agoraToken); // ✅ Frontend ko chahiye
            rs.setAgoraAppId(agoraConfig.getAppId()); // ✅ Frontend ko chahiye

            return rs;

        } catch (Exception e) {
            log.error("Create Room Exception: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create room: " + e.getMessage());
        }
    }

    @Override
    public JoinRoomResponseDto joinRoom(JoinRoomRequestDto requestDto) {
        try {
            ChatRoom room = chatRoomRepository.findById(requestDto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ Already joined check
            Optional<RoomParticipant> existingParticipant = participantRepository
                    .findTopByRoomRoomIdAndUserUserIdAndStatus(
                            requestDto.getRoomId(),
                            requestDto.getUserId(),
                            ParticipantStatus.ACTIVE);

            if (existingParticipant.isPresent()) {
                RoomParticipant ep = existingParticipant.get();
                // ✅ New token generate karo rejoining ke liye
                String newToken = agoraTokenService.generateToken(
                        room.getAgoraChannelName(), user.getUserId());
                return buildJoinResponse(room, ep, newToken);
            }

            // ✅ Available seat number find karo
            Integer seatNumber = getAvailableSeatNumber(room.getRoomId(), room.getMaxParticipants());

            RoomParticipant participant = new RoomParticipant();
            participant.setRoom(room);
            participant.setUser(user);
            participant.setRole(ParticipantRole.LISTENER);
            participant.setStatus(ParticipantStatus.ACTIVE);
            participant.setJoinedAt(LocalDateTime.now());
            participant.setLastActiveAt(LocalDateTime.now());
            participant.setAgoraUid(String.valueOf(user.getUserId()));
            participant.setIsAudioEnabled(true);
            participant.setIsMuted(false);
            participant.setSeatNumber(seatNumber); // ✅ Seat assign karo

            RoomParticipant savedParticipant = participantRepository.save(participant);

            // ✅ Participant count increment
            room.setCurrentParticipants(room.getCurrentParticipants() + 1);
            ChatRoom updatedRoom = chatRoomRepository.save(room);

            // ✅ User ke liye Agora token
            String agoraToken = agoraTokenService.generateToken(
                    updatedRoom.getAgoraChannelName(),
                    user.getUserId());

            return buildJoinResponse(updatedRoom, savedParticipant, agoraToken);

        } catch (Exception e) {
            log.error("❌ Join room error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public LeaveRoomResponseDto leaveRoom(LeaveRoomRequestDto requestDto) {
        try {
            log.info("🚪 User {} leaving room {}",
                    requestDto.getUserId(), requestDto.getRoomId());

            ChatRoom room = chatRoomRepository.findById(requestDto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ DB se active participant fetch karo
            Optional<RoomParticipant> participantOpt = participantRepository
                    .findTopByRoomRoomIdAndUserUserIdAndStatus(
                            requestDto.getRoomId(),
                            requestDto.getUserId(),
                            ParticipantStatus.ACTIVE);

            LocalDateTime joinedAt = LocalDateTime.now();
            LocalDateTime leftAt = LocalDateTime.now();
            long sessionDurationSeconds = 0;

            if (participantOpt.isPresent()) {
                RoomParticipant participant = participantOpt.get();
                joinedAt = participant.getJoinedAt() != null ? participant.getJoinedAt() : LocalDateTime.now();
                leftAt = LocalDateTime.now();
                sessionDurationSeconds = java.time.Duration.between(joinedAt, leftAt).getSeconds();

                // ✅ Status update karo
                participant.setStatus(ParticipantStatus.LEFT);
                participant.setLastActiveAt(leftAt);
                participantRepository.save(participant);

                // ✅ Participant count decrement karo
                int current = room.getCurrentParticipants();
                room.setCurrentParticipants(Math.max(0, current - 1)); // ✅ 0 se neeche nahi
                chatRoomRepository.save(room);

                log.info("✅ Participant record updated for user {}", requestDto.getUserId());
            } else {
                log.warn("⚠️ Active participant not found for user {} in room {}",
                        requestDto.getUserId(), requestDto.getRoomId());
            }

            LeaveRoomResponseDto response = LeaveRoomResponseDto.builder()
                    .roomId(room.getRoomId())
                    .userId(user.getUserId())
                    .userName(user.getFullName())
                    .joinedAt(joinedAt)
                    .leftAt(leftAt)
                    .sessionDurationSeconds(sessionDurationSeconds)
                    .leaveReason(requestDto.getLeaveReason() != null ? requestDto.getLeaveReason() : "VOLUNTARY")
                    .wasKicked(false)
                    .wasBanned(false)
                    .remainingParticipants(room.getCurrentParticipants())
                    .roomStillActive(room.getIsActive())
                    .message("You have left the room successfully!")
                    .success(true)
                    .build();

            log.info("✅ User {} left room {}", user.getUserId(), room.getRoomId());
            return response;

        } catch (Exception e) {
            log.error("💥 Leave room error: {}", e.getMessage(), e);
            return LeaveRoomResponseDto.builder()
                    .success(false)
                    .message("Error leaving room: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public GetRoomListResponseDto getRoomList(GetRoomListRequestDto requestDto) {
        log.info("get list of rooms request: {}", requestDto);
        try {
            Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize());
            List<ChatRoom> listRooms = this.chatRoomRepository.findAll(pageable).getContent();

            List<RoomSummaryDto> listRoomsResponse = listRooms.stream()
                    .map(e -> {
                        RoomSummaryDto dto = new RoomSummaryDto();
                        dto.setRoomId(e.getRoomId());
                        dto.setRoomName(e.getRoomName());
                        dto.setRoomImage(e.getRoomImage() != null ? e.getRoomImage() : "");
                        dto.setDescription(e.getDescription());
                        dto.setHostName(e.getHostName());
                        dto.setHostId(e.getHostId());
                        dto.setCategory(!ObjectUtils.isEmpty(e.getCategory()) ? e.getCategory().name() : "");
                        dto.setRoomType(!ObjectUtils.isEmpty(e.getRoomType()) ? e.getRoomType().name() : "");
                        dto.setCurrentParticipants(e.getCurrentParticipants());
                        dto.setMaxParticipants(e.getMaxParticipants());
                        dto.setIsActive(e.getIsActive());
                        dto.setIsLocked(e.getIsLocked());
                        dto.setCanJoin(Boolean.TRUE.equals(e.getIsActive()) && !Boolean.TRUE.equals(e.getIsLocked()));
                        // dto.setCreatedAt(e.getCreatedAt());
                        return dto;
                    }).collect(Collectors.toList());

            GetRoomListResponseDto responseDto = new GetRoomListResponseDto();
            responseDto.setRooms(listRoomsResponse);
            responseDto.setCurrentPage(requestDto.getPage());
            responseDto.setPageSize(requestDto.getSize());
            responseDto.setTotalElements((long) listRoomsResponse.size());

            return responseDto;
        } catch (Exception e) {
            log.error("getRoomList error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public GetRoomDetailsResponseDto getRoomDetails(GetRoomDetailsRequestDto requestDto) {
        log.info("Get room details request: {}", requestDto);
        try {
            ChatRoom room = chatRoomRepository.findById(requestDto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found: " + requestDto.getRoomId()));

            User hostUser = userRepository.findById(room.getHostId())
                    .orElseThrow(() -> new RuntimeException("Host not found: " + room.getHostId()));

            GetRoomDetailsResponseDto responseDto = new GetRoomDetailsResponseDto();
            responseDto.setRoomId(room.getRoomId());
            responseDto.setRoomName(room.getRoomName());
            responseDto.setRoomImage(room.getRoomImage() != null ? room.getRoomImage() : "");
            responseDto.setDescription(room.getDescription());
            responseDto.setRoomType(room.getRoomType() != null ? room.getRoomType().name() : null);
            responseDto.setCategory(room.getCategory() != null ? room.getCategory().name() : null);
            responseDto.setBackgroundMusic(room.getBackgroundMusic());
            responseDto.setHostImage(hostUser.getImage() != null ? hostUser.getImage() : "");
            responseDto.setHostName(hostUser.getFullName() != null ? hostUser.getFullName() : "");
            responseDto.setMaxParticipants(room.getMaxParticipants());
            responseDto.setCurrentParticipants(room.getCurrentParticipants());
            responseDto.setIsActive(room.getIsActive());
            responseDto.setIsLocked(room.getIsLocked());
            responseDto.setAgoraChannelName(room.getAgoraChannelName());
            responseDto.setCreatedAt(room.getCreatedAt());
            responseDto.setMessage("Room details fetched successfully");
            responseDto.setSuccess(true);

            return responseDto;

        } catch (Exception e) {
            log.error("❌ getRoomDetails error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantResponseDto> getRoomParticipants(Long roomId) {
        try {
            List<RoomParticipant> activeParticipants = participantRepository
                    .findByRoomRoomIdAndStatus(roomId, ParticipantStatus.ACTIVE);

            return activeParticipants.stream().map(p -> {
                ParticipantResponseDto dto = new ParticipantResponseDto();
                dto.setParticipantId(p.getId());
                dto.setUserId(p.getUser().getUserId());
                dto.setFullName(p.getUser().getFullName());
                dto.setUserImage(p.getUser().getImage());
                dto.setRole(p.getRole().name());
                dto.setStatus(p.getStatus().name());
                dto.setSeatNumber(p.getSeatNumber());
                dto.setIsMuted(p.getIsMuted());
                dto.setIsAudioEnabled(p.getIsAudioEnabled());
                dto.setAgoraUid(p.getAgoraUid());
                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("getRoomParticipants error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ParticipantResponseDto joinSeat(JoinRoomSeatRequestDto requestDto) {
        try {
            // ✅ Seat already occupied check
            List<RoomParticipant> activeParticipants = participantRepository
                    .findByRoomRoomIdAndStatus(requestDto.getRoomId(), ParticipantStatus.ACTIVE);

            boolean seatOccupied = activeParticipants.stream()
                    .anyMatch(p -> requestDto.getSeatNumber().equals(p.getSeatNumber())
                            && !p.getUser().getUserId().equals(requestDto.getUserId()));

            if (seatOccupied) {
                throw new RuntimeException("Seat " + requestDto.getSeatNumber() + " is already occupied");
            }

            // ✅ User ka participant record fetch karo
            RoomParticipant participant = participantRepository
                    .findTopByRoomRoomIdAndUserUserIdAndStatus(requestDto.getRoomId(), requestDto.getUserId(),
                            ParticipantStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("User not in room"));

            // ✅ Seat assign + mic enable karo
            participant.setSeatNumber(requestDto.getSeatNumber());
            participant.setIsAudioEnabled(true);
            participant.setIsMuted(false);
            participant.setRole(ParticipantRole.SPEAKER);
            participantRepository.save(participant);

            ParticipantResponseDto dto = new ParticipantResponseDto();
            dto.setParticipantId(participant.getId());
            dto.setUserId(participant.getUser().getUserId());
            dto.setFullName(participant.getUser().getFullName());
            dto.setUserImage(participant.getUser().getImage());
            dto.setRole(participant.getRole().name());
            dto.setStatus(participant.getStatus().name());
            dto.setSeatNumber(participant.getSeatNumber());
            dto.setIsMuted(participant.getIsMuted());
            dto.setIsAudioEnabled(participant.getIsAudioEnabled());
            dto.setAgoraUid(participant.getAgoraUid());

            log.info("✅ User {} joined seat {} in room {}", requestDto.getUserId(), requestDto.getSeatNumber(),
                    requestDto.getRoomId());
            return dto;

        } catch (Exception e) {
            log.error("joinSeat error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void leaveSeat(LeaveRoomSeatRequestDto requestDto) {
        try {
            RoomParticipant participant = participantRepository
                    .findTopByRoomRoomIdAndUserUserIdAndStatus(requestDto.getRoomId(), requestDto.getUserId(),
                            ParticipantStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("User not in room"));

            // ✅ Seat clear + mic disable karo
            participant.setSeatNumber(null);
            participant.setIsAudioEnabled(false);
            participant.setIsMuted(true);
            participant.setRole(ParticipantRole.LISTENER);
            participantRepository.save(participant);

            log.info("✅ User {} left seat in room {}", requestDto.getUserId(), requestDto.getRoomId());
        } catch (Exception e) {
            log.error("leaveSeat error: {}", e.getMessage(), e);
            throw e;
        }
    }


    // ✅ Helper method — response banao
    private JoinRoomResponseDto buildJoinResponse(ChatRoom room,
            RoomParticipant participant, String agoraToken) {
        return JoinRoomResponseDto.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .description(room.getDescription())
                .category(room.getCategory() != null ? room.getCategory().name() : "")
                .roomType(room.getRoomType() != null ? room.getRoomType().name() : "")
                .channelName(room.getAgoraChannelName()) // ✅
                .agoraToken(agoraToken) // ✅
                .agoraAppId(agoraConfig.getAppId()) // ✅
                .tokenExpiryTime(LocalDateTime.now().plusHours(24))
                .agoraUid(participant.getAgoraUid())
                .participantId(participant.getId())
                .role(participant.getRole().name())
                .status(participant.getStatus().name())
                .seatNumber(participant.getSeatNumber())
                .isMuted(participant.getIsMuted())
                .currentParticipants(room.getCurrentParticipants())
                .maxParticipants(room.getMaxParticipants())
                .isRoomLocked(room.getIsLocked())
                .hostId(room.getHostId())
                .hostName(room.getHostName())
                .backgroundMusic(room.getBackgroundMusic())
                .message("Successfully joined! Welcome to " + room.getRoomName())
                .success(true)
                .joinedAt(participant.getJoinedAt())
                .roomImage(room.getRoomImage())
                .hostImage(participant.getUser().getImage())
                .build();
    }

    // ✅ Available seat find karo
    private Integer getAvailableSeatNumber(Long roomId, Integer maxSeats) {
        // Occupied seats fetch karo
        List<RoomParticipant> activeParticipants = participantRepository
                .findByRoomRoomIdAndStatus(roomId, ParticipantStatus.ACTIVE);

        Set<Integer> occupiedSeats = activeParticipants.stream()
                .filter(p -> p.getSeatNumber() != null)
                .map(RoomParticipant::getSeatNumber)
                .collect(Collectors.toSet());

        // ✅ 1 se maxSeats tak pehli available seat return karo
        int max = maxSeats != null ? maxSeats : 12;
        for (int i = 1; i <= max; i++) {
            if (!occupiedSeats.contains(i)) {
                return i;
            }
        }

        return null; // Room full
    }

}
