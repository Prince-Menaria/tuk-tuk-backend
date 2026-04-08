package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.config.AgoraConfig;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.AgoraTokenService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.RtcTokenBuilder;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgoraTokenServiceImpl implements AgoraTokenService {

    private final AgoraConfig agoraConfig;

    @Override
    public String generateToken(String channelName, Long userId) {
        try {
            int expireTimestamp = Utils.getTimestamp() + agoraConfig.getTokenExpiry();

            RtcTokenBuilder builder = new RtcTokenBuilder();
            String token = builder.buildTokenWithUid(
                    agoraConfig.getAppId(),
                    agoraConfig.getAppCertificate(),
                    channelName,
                    userId.intValue(),
                    RtcTokenBuilder.Role.Role_Publisher,
                    expireTimestamp);

            log.info("✅ Agora token generated for channel: {} user: {}", channelName, userId);
            return token;

        } catch (Exception e) {
            log.error("❌ Token generation failed: {}", e);
            throw new RuntimeException("Token generation failed: " + e);
        }
    }

    @Override
    public String generateChannelName(Long roomId) {
        try {

            return "room_" + roomId + "_" + System.currentTimeMillis();
        } catch (Exception e) {
            log.error("❌ Token Generate Channel Name failed: {}", e);
            throw new RuntimeException("Token Generate Channel Name failed: " + e);
        }
    }

}
