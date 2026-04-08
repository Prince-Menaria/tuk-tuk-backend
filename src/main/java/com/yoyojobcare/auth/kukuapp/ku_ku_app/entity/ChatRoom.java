// ChatRoom Entity
package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.RoomCategory;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.RoomType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chat_rooms")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private RoomType roomType = RoomType.PUBLIC;

    @Enumerated(EnumType.STRING)
    private RoomCategory category = RoomCategory.RECOMMEND;

    @Column(nullable = false)
    private Long hostId;

    private String hostName;
    private String roomPassword; // For private rooms
    private String backgroundMusic;

    @Column(name = "room_image", columnDefinition = "LONGTEXT")  // ✅ add karo
    private String roomImage;

    private Integer maxParticipants = 12;
    private Integer currentParticipants = 0;

    private Boolean isActive = true;
    private Boolean isLocked = false;

    // Agora channel details
    @Column(unique = true)
    private String agoraChannelName;
    private String agoraToken;
    private LocalDateTime tokenExpiryTime;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();
}

