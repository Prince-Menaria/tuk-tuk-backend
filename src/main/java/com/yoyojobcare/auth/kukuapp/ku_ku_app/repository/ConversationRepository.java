package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // ✅ User ki conversations list
    @Query("""
        SELECT c FROM Conversation c
        JOIN FETCH c.user1 JOIN FETCH c.user2
        WHERE c.user1.userId = :userId OR c.user2.userId = :userId
        ORDER BY c.lastMessageAt DESC
        """)
    List<Conversation> findUserConversations(@Param("userId") Long userId);

    // ✅ Do specific users ke beech conversation
    @Query("""
        SELECT c FROM Conversation c
        WHERE (c.user1.userId = :u1 AND c.user2.userId = :u2)
           OR (c.user1.userId = :u2 AND c.user2.userId = :u1)
        """)
    Optional<Conversation> findBetweenUsers(@Param("u1") Long u1, @Param("u2") Long u2);
}
