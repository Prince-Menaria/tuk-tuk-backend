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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conversations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    // ✅ Last message info — ChatList mein dikhane ke liye
    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    private LocalDateTime lastMessageAt;

    private Integer unreadCountUser1 = 0; // user1 ke liye unread

    private Integer unreadCountUser2 = 0; // user2 ke liye unread
}
