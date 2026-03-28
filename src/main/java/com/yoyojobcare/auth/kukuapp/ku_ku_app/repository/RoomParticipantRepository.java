package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.RoomParticipant;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    // Optional<RoomParticipant> findByRoomRooIdAndUserUserId(Long roomId, Long userId);

}
