package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RoomParticipant;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.ParticipantStatus;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    // ✅ Active participant find karo
    Optional<RoomParticipant> findTopByRoomRoomIdAndUserUserIdAndStatus(
        Long roomId, Long userId, ParticipantStatus status
    );

    // ✅ Room ke saare active participants
    List<RoomParticipant> findByRoomRoomIdAndStatus(Long roomId, ParticipantStatus status);

}
