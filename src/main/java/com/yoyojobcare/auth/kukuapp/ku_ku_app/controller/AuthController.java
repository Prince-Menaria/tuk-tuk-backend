package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.security.JwtTokenProvider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.AuthService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "JWT-based authentication endpoints")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Operation(summary = "Get Current User", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<MobileResponse<Map<String, Object>>> getCurrentUser(
            @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                throw new RuntimeException("User not authenticated");
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("email", user.getEmail());
            userInfo.put("fullName", user.getFullName());
            userInfo.put("image", user.getImage());
            userInfo.put("gender", user.getGender());
            userInfo.put("provider", user.getProvider());
            userInfo.put("roles", user.getRoles().stream()
                .map(role -> role.getName()).toArray());

            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setData(userInfo);
            response.setMessage("User details retrieved successfully");
            response.setStatus(true);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Get current user failed: {}", e.getMessage(), e);
            
            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setMessage("Failed to get user details: " + e.getMessage());
            response.setStatus(false);
            
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Refresh Access Token")
    @PostMapping("/refresh")
    public ResponseEntity<MobileResponse<Map<String, Object>>> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        try {
            log.info("🔄 Refresh token request");

            if (!jwtTokenProvider.validateToken(request.refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String tokenType = jwtTokenProvider.getTokenType(request.refreshToken);
            if (!"REFRESH".equals(tokenType)) {
                throw new RuntimeException("Invalid token type");
            }

            Map<String, Object> tokens = authService.refreshTokens(request.refreshToken);

            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setData(tokens);
            response.setMessage("Tokens refreshed successfully");
            response.setStatus(true);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Token refresh failed: {}", e.getMessage(), e);
            
            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setMessage("Token refresh failed: " + e.getMessage());
            response.setStatus(false);
            
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Validate Token")
    @PostMapping("/validate")
    public ResponseEntity<MobileResponse<Map<String, Object>>> validateToken(
            @RequestBody ValidateTokenRequest request) {
        try {
            boolean isValid = jwtTokenProvider.validateToken(request.token);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", isValid);
            
            if (isValid) {
                Long userId = jwtTokenProvider.getUserIdFromToken(request.token);
                String email = jwtTokenProvider.getEmailFromToken(request.token);
                result.put("userId", userId);
                result.put("email", email);
            }

            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setData(result);
            response.setMessage(isValid ? "Token is valid" : "Token is invalid");
            response.setStatus(isValid);

            return new ResponseEntity<>(response, isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            log.error("❌ Token validation failed: {}", e.getMessage(), e);
            
            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setMessage("Token validation failed: " + e.getMessage());
            response.setStatus(false);
            
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Dashboard (Protected)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/dashboard")
    public ResponseEntity<MobileResponse<Map<String, Object>>> dashboard(
            @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                throw new RuntimeException("User not authenticated");
            }

            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("welcomeMessage", "Welcome to TukTuk Chat!");
            dashboardData.put("user", Map.of(
                "name", user.getFullName(),
                "email", user.getEmail(),
                "picture", user.getImage()
            ));

            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setData(dashboardData);
            response.setMessage("Dashboard data retrieved successfully");
            response.setStatus(true);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Dashboard access failed: {}", e.getMessage(), e);
            
            MobileResponse<Map<String, Object>> response = new MobileResponse<>();
            response.setMessage("Dashboard access failed: " + e.getMessage());
            response.setStatus(false);
            
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World - Public Endpoint";
    }

    // Request DTOs
    static class RefreshTokenRequest {
        public String refreshToken;
    }

    static class ValidateTokenRequest {
        public String token;
    }
}