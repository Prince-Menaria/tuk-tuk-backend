package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{

    List<ChatRoom> findByHostId(Long hostId);



}
