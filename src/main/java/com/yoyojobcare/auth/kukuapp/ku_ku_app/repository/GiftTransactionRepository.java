package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.GiftTransaction;

public interface GiftTransactionRepository extends JpaRepository<GiftTransaction, Long> {
    
    List<GiftTransaction> findByRoomRoomIdOrderBySentAtDesc(Long roomId);

    List<GiftTransaction> findBySenderUserIdOrderBySentAtDesc(Long senderId);

    List<GiftTransaction> findByReceiverUserIdOrderBySentAtDesc(Long receiverId);

    
}
