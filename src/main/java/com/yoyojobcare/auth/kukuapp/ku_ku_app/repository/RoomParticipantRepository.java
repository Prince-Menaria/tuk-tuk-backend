package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RoomParticipant;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.enumEntity.ParticipantStatus;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    // ✅ JOIN FETCH — LazyInitializationException fix
    @Query("SELECT p FROM RoomParticipant p JOIN FETCH p.user WHERE p.room.roomId = :roomId AND p.status = :status")
    List<RoomParticipant> findByRoomRoomIdAndStatus(
        @Param("roomId") Long roomId,
        @Param("status") ParticipantStatus status
    );

    Optional<RoomParticipant> findTopByRoomRoomIdAndUserUserIdAndStatus(
        Long roomId, Long userId, ParticipantStatus status
    );

}
