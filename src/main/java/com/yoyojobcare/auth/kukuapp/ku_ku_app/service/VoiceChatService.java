package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.List;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.CreateRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.EditRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.GetRoomDetailsRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.GetRoomListRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.GetSearchByHostIdRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.JoinRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.JoinRoomSeatRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.LeaveRoomRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.voiceChat.LeaveRoomSeatRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.CreateRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.EditRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.GetRoomDetailsResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.GetRoomListResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.GetSearchByHostIdResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.JoinRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.LeaveRoomResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.voiceChat.ParticipantResponseDto;

public interface VoiceChatService {

    public CreateRoomResponseDto createRoom(CreateRoomRequestDto requestDto);

    public JoinRoomResponseDto joinRoom(JoinRoomRequestDto requestDto);

    public LeaveRoomResponseDto leaveRoom(LeaveRoomRequestDto requestDto);

    public GetRoomListResponseDto getRoomList(GetRoomListRequestDto requestDto);

    public GetRoomDetailsResponseDto getRoomDetails(GetRoomDetailsRequestDto requestDto);

    public List<ParticipantResponseDto> getRoomParticipants(Long roomId);

    public ParticipantResponseDto joinSeat(JoinRoomSeatRequestDto requestDto);

    public void leaveSeat(LeaveRoomSeatRequestDto requestDto);

    public EditRoomResponseDto editRoom(EditRoomRequestDto requestDto);

    public List<GetSearchByHostIdResponseDto> searchByHostId(GetSearchByHostIdRequestDto requestDto);

    

}
