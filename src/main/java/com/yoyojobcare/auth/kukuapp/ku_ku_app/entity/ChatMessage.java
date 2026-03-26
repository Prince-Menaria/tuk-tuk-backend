// package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

// import java.time.LocalDateTime;
// import jakarta.persistence.*;
// import lombok.Data;
// import lombok.EqualsAndHashCode;

// @Entity
// @Table(name = "chat_messages")
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class ChatMessage extends BaseEntity {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long messageId;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "room_id", nullable = false)
//     private ChatRoom room;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "sender_id", nullable = false)
//     private User sender;
    
//     @Enumerated(EnumType.STRING)
//     private MessageType messageType = MessageType.TEXT;
    
//     @Column(columnDefinition = "TEXT")
//     private String content;
    
//     private String emoji;
//     private String giftId;
//     private String imageUrl;
    
//     private LocalDateTime timestamp = LocalDateTime.now();
//     private Boolean isDeleted = false;
// }

// public enum MessageType {
//     TEXT, EMOJI, GIFT, SYSTEM, IMAGE
// }