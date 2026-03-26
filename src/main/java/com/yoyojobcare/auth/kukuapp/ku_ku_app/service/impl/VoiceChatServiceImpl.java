package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.ChatMessage;
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
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.VoiceChatService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.CreateRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.JoinRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.CreateRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.JoinRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.MessageSummaryDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.ParticipantSummaryDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoiceChatServiceImpl implements VoiceChatService {

    // ============ Repository Dependencies ============
    // सभी database operations के लिए repositories को inject करना
    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository messageRepository;

    // ============ External Service Dependencies ============
    // private final SimpMessagingTemplate messagingTemplate; // Real-time messaging
    // के लिए WebSocket
    // private final AgoraConfig agoraConfig; // Voice chat token generation के लिए

    @Override
    public CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto) {
        try {
            // Step 1: Input validation और logging
            log.info("🏗️ Creating room for user: {} with name: {}",
                    requestDto.getHostId(), requestDto.getRoomName());

            // // Basic validation check
            // if (!requestDto.isValid()) {
            // throw new VoiceChatException("Invalid room creation request");
            // }

            // Step 2: Host user validation - database से user exist करता है या नहीं check
            // करना
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
            // Room capacity और settings
            room.setMaxParticipants(requestDto.getMaxParticipants() != null ? requestDto.getMaxParticipants() : 12); // Default
                                                                                                                     // 12
                                                                                                                     // participants
            room.setCurrentParticipants(0);

            try {
                room.setRoomType(RoomType.valueOf(requestDto.getRoomType().toUpperCase()));
                room.setCategory(RoomCategory.valueOf(requestDto.getCategory().toUpperCase()));
            } catch (Exception e) {
                log.error("Invalid room type or category : " + e);
            }
            // Step 6: Database में room save करना
            ChatRoom savedRoom = chatRoomRepository.save(room);
            log.info("✅ Room saved with ID: {}", savedRoom.getRoomId());

            RoomType roomType = savedRoom.getRoomType();

            CreateRoomResponseDto rs = new CreateRoomResponseDto();
            rs.setRoomId(savedRoom.getRoomId());
            rs.setRoomImage(savedRoom.getRoomImage());
            rs.setRoomName(savedRoom.getRoomName());
            rs.setRoomType(roomType.name());
            rs.setDescription(savedRoom.getDescription());
            rs.setHostId(savedRoom.getHostId());
            rs.setHostName(savedRoom.getHostName());
            rs.setHostImage(hostUser.getImage());

            log.info("🎊 Room creation completed successfully for room ID: {}", savedRoom.getRoomId());
            return rs;

        } catch (Exception e) {
            log.error("Create Room Exception : " + e);
        }
        return null;
    }

    @Override
    public JoinRoomResponseDto joinRoom(JoinRoomRequestDto requestDto) {
        try {
            log.info("🚪 User {} attempting to join room {}",
                    requestDto.getUserId(), requestDto.getRoomId());

            // Step 1: Room और user validation
            ChatRoom room = chatRoomRepository.findById(requestDto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found with ID: " + requestDto.getRoomId()));

            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + requestDto.getUserId()));

            // Step 2: Room joinability checks
            // validateRoomJoinability(room, user, requestDto);

            // Step 3: User's previous participation check
            // Optional<RoomParticipant> existingParticipant =
            // participantRepository.findByRoomAndUser(room, user);

            // Check for existing active participation
            // if (existingParticipant.isPresent() &&
            // existingParticipant.get().getStatus() == ParticipantStatus.ACTIVE) {
            // throw new VoiceChatException("User is already in this room");
            // }

            // Check for bans या kicks
            // if (existingParticipant.isPresent()) {
            // ParticipantStatus lastStatus = existingParticipant.get().getStatus();
            // if (lastStatus == ParticipantStatus.BANNED) {
            // throw new VoiceChatException("User is banned from this room");
            // } else if (lastStatus == ParticipantStatus.KICKED &&
            // existingParticipant.get().getLeftAt().isAfter(LocalDateTime.now().minusHours(1)))
            // {
            // throw new VoiceChatException("User was recently kicked. Please wait before
            // rejoining.");
            // }
            // }

            // // Step 4: Create या update participant record
            // RoomParticipant participant = existingParticipant.orElse(new
            // RoomParticipant());
            RoomParticipant participant = new RoomParticipant();

            // Participant details set करना
            participant.setRoom(room);
            participant.setUser(user);
            participant.setRole(ParticipantRole.LISTENER); // Default role
            participant.setStatus(ParticipantStatus.ACTIVE);
            participant.setJoinedAt(LocalDateTime.now());
            participant.setLastActiveAt(LocalDateTime.now());
            // participant.setLeftAt(null); // Clear previous left time

            // Agora integration setup
            participant.setAgoraUid(String.valueOf(user.getUserId()));
            // participant.setAgoraRole("audience"); // Default audience role
            participant.setIsAudioEnabled(true);
            // participant.setCanSpeak(false); // Initially in audience
            // participant.setCanChat(true);
            // participant.setCanSendPrivateMessage(true);

            // Behavior score reset (if rejoining)
            // if (existingParticipant.isPresent()) {
            // participant.setBehaviorScore(Math.max(participant.getBehaviorScore(), 50));
            // // Minimum 50 on rejoin
            // }

            // Step 5: Save participant
            RoomParticipant savedParticipant = participantRepository.save(participant);

            // Step 6: Update room participant count
            // room.incrementParticipantCount();
            ChatRoom updatedRoom = chatRoomRepository.save(room);

            // Step 7: Generate user-specific Agora token
            // String userToken = generateAgoraTokenForUser(room.getAgoraChannelName(),
            // user.getUserId(), "subscriber");

            // Step 8: Send welcome messages
            // sendSystemMessage(room, "👋 " + user.getFullName() + " कमरे में आ गए हैं!");
            // sendWelcomeMessage(room, user);

            // Step 9: Real-time notifications
            // broadcastParticipantJoined(room, savedParticipant);

            // Step 10: Prepare response data

            // Get other participants list
            // List<RoomParticipant> otherParticipants = participantRepository
            // .findByRoomAndStatus(room, ParticipantStatus.ACTIVE)
            // .stream()
            // .filter(p -> !p.getUser().getUserId().equals(user.getUserId()))
            // .collect(Collectors.toList());

            // List<ParticipantSummaryDto> participantSummaries = otherParticipants.stream()
            // .map(this::convertToParticipantSummary)
            // .collect(Collectors.toList());

            // // Get recent messages
            // List<ChatMessage> recentMessages = messageRepository
            // .findRecentRoomMessages(room.getRoomId(), PageRequest.of(0, 10));

            // List<MessageSummaryDto> messageSummaries = recentMessages.stream()
            // .map(this::convertToMessageSummary)
            // .collect(Collectors.toList());

            // // Get room rules
            // List<String> roomRules = Arrays.asList(
            // "सभी का सम्मान करें",
            // "अभद्र भाषा का उपयोग न करें",
            // "स्पैम न करें",
            // "निजी जानकारी साझा न करें",
            // "होस्ट के नियमों का पालन करें"
            // );

            // Step 11: Build comprehensive response
            JoinRoomResponseDto response = JoinRoomResponseDto.builder()
                    .roomId(updatedRoom.getRoomId())
                    .roomName(updatedRoom.getRoomName())
                    .description(updatedRoom.getDescription())
                    .category(updatedRoom.getCategory().name())
                    .roomType(updatedRoom.getRoomType().name())
                    .channelName(updatedRoom.getAgoraChannelName())
                    // .agoraToken(userToken)
                    .tokenExpiryTime(LocalDateTime.now().plusHours(24))
                    // .agoraAppId(updatedRoom.getAgoraAppId())
                    .agoraUid(savedParticipant.getAgoraUid())
                    .participantId(savedParticipant.getId())
                    .role(savedParticipant.getRole().name())
                    .status(savedParticipant.getStatus().name())
                    .seatNumber(savedParticipant.getSeatNumber())
                    // .canSpeak(savedParticipant.getCanSpeak())
                    .isMuted(savedParticipant.getIsMuted())
                    .currentParticipants(updatedRoom.getCurrentParticipants())
                    .maxParticipants(updatedRoom.getMaxParticipants())
                    // .isRoomFull(updatedRoom.isFull())
                    .isRoomLocked(updatedRoom.getIsLocked())
                    .hostId(updatedRoom.getHostId())
                    .hostName(updatedRoom.getHostName())
                    // .hostImage(getHostImage(updatedRoom.getHostId()))
                    // .roomLanguage(updatedRoom.getRoomLanguage())
                    .backgroundMusic(updatedRoom.getBackgroundMusic())
                    // .welcomeMessage(updatedRoom.getWelcomeMessage())
                    // // .otherParticipants(participantSummaries)
                    // .recentMessages(messageSummaries)
                    // .roomRules(roomRules)
                    .message("Successfully joined the room! Welcome to " + updatedRoom.getRoomName())
                    .success(true)
                    .joinedAt(savedParticipant.getJoinedAt())
                    .build();

            log.info("✅ User {} successfully joined room {} as participant {}",
                    user.getUserId(), room.getRoomId(), savedParticipant.getId());

            return response;

        } catch (Exception e) {
            log.error("❌ Business error joining room: {}", e.getMessage());
            throw e;

        }

    }

}
