package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.DirectMessage;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    // ✅ Do users ke beech messages — latest first
    @Query("""
        SELECT m FROM DirectMessage m
        JOIN FETCH m.sender JOIN FETCH m.receiver
        WHERE ((m.sender.userId = :userId1 AND m.receiver.userId = :userId2)
            OR (m.sender.userId = :userId2 AND m.receiver.userId = :userId1))
        AND m.isDeleted = false
        ORDER BY m.sentAt DESC
        """)
    Page<DirectMessage> findConversationMessages(
        @Param("userId1") Long userId1,
        @Param("userId2") Long userId2,
        Pageable pageable
    );

    // ✅ Unread count
    long countBySenderUserIdAndReceiverUserIdAndIsReadFalse(Long senderId, Long receiverId);
}
