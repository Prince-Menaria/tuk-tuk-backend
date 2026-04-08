package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

public interface AgoraTokenService {

    public String generateToken(String channelName, Long userId);

    public String generateChannelName(Long roomId);

}
