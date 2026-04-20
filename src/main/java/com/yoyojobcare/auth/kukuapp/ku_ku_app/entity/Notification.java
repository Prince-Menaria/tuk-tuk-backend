package com.yoyojobcare.auth.kukuapp.ku_ku_app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    // ✅ Jisko notification milegi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // ✅ Jisne action kiya (optional — system notification mein null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false)
    private String type;
    // POST_LIKE, POST_COMMENT, COMMENT_REPLY,
    // ROOM_GIFT, FOLLOW, ROOM_JOIN, SYSTEM

    @Column(nullable = false)
    private String message;

    // ✅ Related entity ka reference
    private Long referenceId;    // postId / roomId / commentId
    private String referenceType; // POST, ROOM, COMMENT

    private Boolean isRead = false;

    private LocalDateTime notifiedAt = LocalDateTime.now();
}
