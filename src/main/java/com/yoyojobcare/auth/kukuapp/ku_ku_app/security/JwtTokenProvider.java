package com.yoyojobcare.auth.kukuapp.ku_ku_app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:mySecretKey1234567890123456789012345678901234567890}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours
    private int jwtExpirationInMs;

    @Value("${app.jwt.refresh.expiration:604800000}") // 7 days
    private int refreshTokenExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(User user) {
        try {
            Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            return Jwts.builder()
                    .subject(String.valueOf(user.getUserId()))
                    .claim("email", user.getEmail())
                    .claim("fullName", user.getFullName())
                    .claim("image", user.getImage())
                    .claim("roles", roles)
                    .claim("provider", user.getProvider() != null ? user.getProvider().toString() : "LOCAL")
                    .claim("tokenType", "ACCESS")
                    .issuedAt(new Date())
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            log.error("❌ Error generating access token: {}", e.getMessage(), e);
            throw new RuntimeException("Token generation failed");
        }
    }

    public String generateRefreshToken(User user) {
        try {
            Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationInMs);

            return Jwts.builder()
                    .subject(String.valueOf(user.getUserId()))
                    .claim("email", user.getEmail())
                    .claim("tokenType", "REFRESH")
                    .issuedAt(new Date())
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            log.error("❌ Error generating refresh token: {}", e.getMessage(), e);
            throw new RuntimeException("Refresh token generation failed");
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            log.error("❌ Error extracting user ID: {}", e.getMessage());
            return null;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? claims.get("email", String.class) : null;
        } catch (Exception e) {
            log.error("❌ Error extracting email: {}", e.getMessage());
            return null;
        }
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("❌ Error extracting claims: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("❌ Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("❌ Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("❌ Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("❌ Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("❌ JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    public String getTokenType(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? claims.get("tokenType", String.class) : null;
        } catch (Exception e) {
            return null;
        }
    }
}