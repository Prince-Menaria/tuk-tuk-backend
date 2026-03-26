package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.security.JwtTokenProvider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public Map<String, Object> refreshTokens(String refreshToken) {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            if (userId == null) {
                throw new RuntimeException("Invalid user in refresh token");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isEnable()) {
                throw new RuntimeException("User account is disabled");
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            tokens.put("tokenType", "Bearer");
            tokens.put("expiresIn", 86400);

            log.info("✅ Tokens refreshed successfully for user: {}", user.getUserId());
            return tokens;

        } catch (Exception e) {
            log.error("❌ Token refresh failed: {}", e.getMessage(), e);
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
}
