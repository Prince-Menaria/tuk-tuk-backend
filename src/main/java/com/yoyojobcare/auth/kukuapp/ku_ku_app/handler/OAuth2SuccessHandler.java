package com.yoyojobcare.auth.kukuapp.ku_ku_app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Provider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RoleRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.security.JwtTokenProvider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.IdGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final IdGenerator idGenerator; // ← inject करो

    @Value("${app.frontend.url:}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            log.info("🎉 OAuth2 Login successful for: {} ({})", name, email);

            // Find or create user
            User user = userRepository.findByEmail(email)
                    .orElse(null);

            if (ObjectUtils.isEmpty(user)) {
                user = createNewUser(email, name, picture);
                // Update user info if needed
                updateUserInfo(user, name, picture);
                user = userRepository.save(user);
            }

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // Check if request is from Flutter app
            String userAgent = request.getHeader("User-Agent");
            String acceptHeader = request.getHeader("Accept");

            if (isFlutterRequest(userAgent, acceptHeader)) {
                // Flutter के लिए JSON response
                Map<String, Object> tokenResponse = createTokenResponse(user, accessToken, refreshToken);
                sendJsonResponse(response, tokenResponse);
            } else {
                // Web browser के लिए सीधे redirect ← ONLY यही करें
                // String redirectUrl = "http://localhost:3000/oauth/callback"
                String redirectUrl = "https://tuk-tuk-re.vercel.app/oauth/callback"
                        + "?accessToken=" + accessToken
                        + "&refreshToken=" + refreshToken
                        + "&userId=" + user.getUserId()
                        + "&email=" + user.getEmail()
                        + "&success=" + Boolean.TRUE.toString()
                        + "&fullName=" + user.getFullName();

                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            }

            log.info("✅ JWT tokens generated for user: {}", user.getUserId());

        } catch (Exception e) {
            log.error("❌ Error in OAuth2 success handler: {}", e.getMessage(), e);
            handleError(response, e);
        }
    }

    // Flutter ke liye below code hai
    // @Override
    // public void onAuthenticationSuccess(
    // HttpServletRequest request,
    // HttpServletResponse response,
    // Authentication authentication) throws IOException {

    // try {
    // OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    // String email = oAuth2User.getAttribute("email");
    // String name = oAuth2User.getAttribute("name");
    // String picture = oAuth2User.getAttribute("picture");

    // log.info("🎉 OAuth2 Login successful for: {} ({})", name, email);

    // // Find or create user
    // User user = userRepository.findByEmail(email)
    // .orElseGet(() -> createNewUser(email, name, picture));

    // // Update user info if needed
    // updateUserInfo(user, name, picture);
    // user = userRepository.save(user);

    // // Generate JWT tokens
    // String accessToken = jwtTokenProvider.generateAccessToken(user);
    // String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    // // / Frontend पर redirect करें tokens के साथ
    // String redirectUrl = "http://localhost:3000/oauth/callback"
    // + "?accessToken=" + accessToken
    // + "&refreshToken=" + refreshToken;

    // getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    // // Create token response
    // Map<String, Object> tokenResponse = createTokenResponse(user, accessToken,
    // refreshToken);

    // // Check if request is from Flutter app
    // String userAgent = request.getHeader("User-Agent");
    // String acceptHeader = request.getHeader("Accept");

    // if (isFlutterRequest(userAgent, acceptHeader)) {
    // // Return JSON for Flutter
    // sendJsonResponse(response, tokenResponse);
    // } else {
    // // Return HTML for web browsers
    // sendHtmlResponse(response, tokenResponse);
    // }

    // log.info("✅ JWT tokens generated for user: {}", user.getUserId());

    // } catch (Exception e) {
    // log.error("❌ Error in OAuth2 success handler: {}", e.getMessage(), e);
    // handleError(response, e);
    // }
    // }

    private boolean isFlutterRequest(String userAgent, String acceptHeader) {
        // Check if request is from Flutter app
        return (acceptHeader != null && acceptHeader.contains("application/json")) ||
                (userAgent != null && userAgent.toLowerCase().contains("flutter")) ||
                // You can add more Flutter-specific detection logic
                false;
    }

    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> tokenResponse) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", true);
        jsonResponse.put("message", "Login successful");
        jsonResponse.put("data", tokenResponse);

        String jsonString = objectMapper.writeValueAsString(jsonResponse);
        response.getWriter().write(jsonString);
        response.getWriter().flush();

        log.info("📱 JSON response sent for Flutter app");
    }

    private void sendHtmlResponse(HttpServletResponse response, Map<String, Object> tokenResponse) throws IOException {
        // Check if we should redirect to frontend
        if (frontendUrl != null && !frontendUrl.isEmpty()) {
            String redirectUrl = buildFrontendRedirectUrl(tokenResponse);
            response.sendRedirect(redirectUrl);
            log.info("🌐 Redirecting to frontend: {}", redirectUrl);
        } else {
            // Send HTML page with tokens
            String htmlResponse = generateSuccessPage(tokenResponse);
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(htmlResponse);
            response.getWriter().flush();
            log.info("🌐 HTML response sent for web browser");
        }
    }

    private String buildFrontendRedirectUrl(Map<String, Object> tokenResponse) {
        // Build URL with tokens as query parameters (for frontend)
        StringBuilder url = new StringBuilder(frontendUrl);
        url.append("?success=true");
        url.append("&accessToken=").append(tokenResponse.get("accessToken"));
        url.append("&refreshToken=").append(tokenResponse.get("refreshToken"));

        Map<?, ?> user = (Map<?, ?>) tokenResponse.get("user");
        url.append("&userId=").append(user.get("userId"));
        url.append("&email=").append(user.get("email"));
        url.append("&fullName=").append(user.get("fullName"));
        url.append("&image=").append(user.get("image"));

        return url.toString();
    }

    // ... (rest of your existing methods remain the same)

    private User createNewUser(String email, String name, String picture) {
        try {
            User user = new User();
            user.setUserId(this.idGenerator.generate6DigitUserId()); // ← use करो
            user.setEmail(email);
            user.setFullName(name);
            user.setImage(picture);
            user.setProvider(Provider.GOOGLE);
            user.setEnable(true);

            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> createDefaultRole());

            user.setRoles(Set.of(userRole));
            log.info("🆕 Creating new user: {}", email);
            return user;
        } catch (Exception e) {
            log.error("❌ Error creating new user: {}", e.getMessage(), e);
            throw new RuntimeException("User creation failed: " + e.getMessage());
        }
    }

    private Role createDefaultRole() {
        Role newRole = new Role();
        newRole.setName("USER");
        return roleRepository.save(newRole);
    }

    private void updateUserInfo(User user, String name, String picture) {
        boolean updated = false;

        if (user.getFullName() == null || !user.getFullName().equals(name)) {
            user.setFullName(name);
            updated = true;
        }
        if (user.getImage() == null || !user.getImage().equals(picture)) {
            user.setImage(picture);
            updated = true;
        }
        if (user.getProvider() == null) {
            user.setProvider(Provider.GOOGLE);
            updated = true;
        }

        if (updated) {
            log.info("📝 Updated user info for: {}", user.getEmail());
        }
    }

    private Map<String, Object> createTokenResponse(User user, String accessToken, String refreshToken) {
        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("accessToken", accessToken);
        tokenResponse.put("refreshToken", refreshToken);
        tokenResponse.put("tokenType", "Bearer");
        tokenResponse.put("expiresIn", 86400);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("email", user.getEmail());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("image", user.getImage());
        userInfo.put("provider", user.getProvider());
        userInfo.put("roles", user.getRoles().stream().map(Role::getName).toArray());

        tokenResponse.put("user", userInfo);
        return tokenResponse;
    }

    private String generateSuccessPage(Map<String, Object> response) {
        try {
            // Your existing HTML generation code
            return "<!DOCTYPE html><html><head><title>Success</title></head><body>" +
                    "<h1>Login Successful!</h1>" +
                    "<script>console.log(" + objectMapper.writeValueAsString(response) + ");</script>" +
                    "</body></html>";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json; charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", e.getMessage());

        String jsonString = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonString);
    }
}