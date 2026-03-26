// package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

// import java.time.LocalDateTime;
// import jakarta.persistence.*;
// import lombok.Data;
// import lombok.EqualsAndHashCode;

// @Entity
// @Table(name = "room_participants")
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class RoomParticipant extends BaseEntity {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "room_id", nullable = false)
//     private ChatRoom room;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id", nullable = false)
//     private User user;
    
//     @Enumerated(EnumType.STRING)
//     private ParticipantRole role = ParticipantRole.LISTENER;
    
//     @Enumerated(EnumType.STRING)
//     private ParticipantStatus status = ParticipantStatus.ACTIVE;
    
//     private Integer seatNumber; // For mic seats (1-12)
//     private Boolean isMuted = false;
//     private Boolean isHandRaised = false;
//     private LocalDateTime joinedAt = LocalDateTime.now();
//     private LocalDateTime lastActiveAt = LocalDateTime.now();
    
//     // Agora specific
//     private String agoraUid;
//     private Boolean isAudioEnabled = true;
// }

// public enum ParticipantRole {
//     HOST, CO_HOST, SPEAKER, LISTENER
// }

// public enum ParticipantStatus {
//     ACTIVE, MUTED, KICKED, LEFT
// }