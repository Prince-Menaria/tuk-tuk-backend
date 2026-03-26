package com.yoyojobcare.auth.kukuapp.ku_ku_app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Provider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RoleRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ObjectMapper objectMapper;

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
                    .orElseGet(() -> createNewUser(email, name, picture));

            // Update user info if needed
            updateUserInfo(user, name, picture);
            user = userRepository.save(user);

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // Create token response
            Map<String, Object> tokenResponse = createTokenResponse(user, accessToken, refreshToken);

            // Generate HTML response with tokens
            String htmlResponse = generateSuccessPage(tokenResponse);

            // Set response
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(htmlResponse);
            response.getWriter().flush();

            log.info("✅ JWT tokens generated and displayed for user: {}", user.getUserId());

        } catch (Exception e) {
            log.error("❌ Error in OAuth2 success handler: {}", e.getMessage(), e);
            handleError(response, e);
        }
    }

    private User createNewUser(String email, String name, String picture) {
        try {
            User user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setImage(picture);
            user.setProvider(Provider.GOOGLE);
            user.setEnable(true);

            // Assign default role
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
        Map<?, ?> user = (Map<?, ?>) response.get("user");
        
        StringBuilder html = new StringBuilder();
        
        // HTML Header
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Login Success - TukTuk Chat</title>");
        html.append(getCSS());
        html.append("</head>");
        
        // HTML Body
        html.append("<body>");
        html.append("<div class='container'>");
        html.append(getHeader());
        html.append("<div class='content'>");
        html.append(getUserInfo(user));
        html.append(getTokenSection("Access Token", "accessToken", (String) response.get("accessToken")));
        html.append(getTokenSection("Refresh Token", "refreshToken", (String) response.get("refreshToken")));
        html.append(getInstructions(user));
        html.append(getJsonResponse(response));
        html.append("</div>");
        html.append("</div>");
        html.append(getJavaScript());
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }

    private String getCSS() {
        return "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 20px; }" +
                ".container { max-width: 800px; margin: 0 auto; background: white; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #28a745, #20c997); color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 30px; }" +
                ".user-info { background: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 25px; border-left: 5px solid #28a745; }" +
                ".token-section { margin-bottom: 25px; }" +
                ".token-box { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 15px; font-family: monospace; font-size: 12px; word-break: break-all; margin: 10px 0; }" +
                ".copy-btn { background: #007bff; color: white; border: none; padding: 8px 15px; border-radius: 5px; cursor: pointer; font-size: 12px; margin-top: 10px; }" +
                ".copy-btn:hover { background: #0056b3; }" +
                ".success-icon { font-size: 48px; margin-bottom: 15px; }" +
                ".instructions { background: #e7f3ff; border: 1px solid #b6d7ff; border-radius: 8px; padding: 20px; margin-top: 20px; }" +
                ".endpoint { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 8px 12px; font-family: monospace; font-size: 13px; margin: 5px 0; }" +
                "h3 { color: #333; margin-bottom: 10px; }" +
                ".json-response { background: #2d3748; color: #e2e8f0; padding: 20px; border-radius: 8px; font-family: monospace; font-size: 12px; white-space: pre-wrap; margin: 15px 0; overflow-x: auto; }" +
                "</style>";
    }

    private String getHeader() {
        return "<div class='header'>" +
                "<div class='success-icon'>🎉</div>" +
                "<h1>Login Successful!</h1>" +
                "<p>Welcome to TukTuk Chat</p>" +
                "</div>";
    }

    private String getUserInfo(Map<?, ?> user) {
        return "<div class='user-info'>" +
                "<h3>👤 User Information</h3>" +
                "<p><strong>User ID:</strong> " + user.get("userId") + "</p>" +
                "<p><strong>Name:</strong> " + user.get("fullName") + "</p>" +
                "<p><strong>Email:</strong> " + user.get("email") + "</p>" +
                "<p><strong>Provider:</strong> " + user.get("provider") + "</p>" +
                "</div>";
    }

    private String getTokenSection(String title, String id, String token) {
        return "<div class='token-section'>" +
                "<h3>🔑 " + title + " (Copy for API requests)</h3>" +
                "<div class='token-box' id='" + id + "'>" + token + "</div>" +
                "<button class='copy-btn' onclick='copyToClipboard(\"" + id + "\")'>📋 Copy " + title + "</button>" +
                "</div>";
    }

    private String getInstructions(Map<?, ?> user) {
        return "<div class='instructions'>" +
                "<h3>📋 How to use these tokens in Postman:</h3>" +
                "<ol>" +
                "<li><strong>Copy the Access Token</strong> above</li>" +
                "<li><strong>In Postman</strong>, go to Headers tab</li>" +
                "<li><strong>Add Header:</strong> <code>Authorization: Bearer [PASTE_ACCESS_TOKEN_HERE]</code></li>" +
                "<li><strong>Test these API endpoints:</strong></li>" +
                "</ol>" +
                "<h4>🔗 API Endpoints to test:</h4>" +
                "<div class='endpoint'>GET http://localhost:8083/api/v1/auth/me</div>" +
                "<div class='endpoint'>GET http://localhost:8083/api/v1/auth/dashboard</div>" +
                "<div class='endpoint'>GET http://localhost:8083/api/v1/user-profile/profile-info-by-id?userId=" + user.get("userId") + "</div>" +
                "<h4>🔄 Refresh Token:</h4>" +
                "<div class='endpoint'>POST http://localhost:8083/api/v1/auth/refresh</div>" +
                "<p><small>Body: {\"refreshToken\": \"YOUR_REFRESH_TOKEN\"}</small></p>" +
                "</div>";
    }

    private String getJsonResponse(Map<String, Object> response) {
        try {
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            return "<div class='json-response'>" + jsonString + "</div>";
        } catch (Exception e) {
            return "<div class='json-response'>" + response.toString() + "</div>";
        }
    }

    private String getJavaScript() {
        return "<script>" +
                "function copyToClipboard(elementId) {" +
                "const element = document.getElementById(elementId);" +
                "const text = element.textContent;" +
                "navigator.clipboard.writeText(text).then(function() {" +
                "alert('✅ Token copied to clipboard!');" +
                "}, function(err) {" +
                "console.error('❌ Could not copy text: ', err);" +
                "const textArea = document.createElement('textarea');" +
                "textArea.value = text;" +
                "document.body.appendChild(textArea);" +
                "textArea.focus();" +
                "textArea.select();" +
                "try {" +
                "document.execCommand('copy');" +
                "alert('✅ Token copied to clipboard!');" +
                "} catch (err) {" +
                "alert('❌ Please copy the token manually');" +
                "}" +
                "document.body.removeChild(textArea);" +
                "});" +
                "}" +
                "</script>";
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html; charset=UTF-8");
        
        String errorPage = "<!DOCTYPE html>" +
                "<html>" +
                "<head><title>Login Error</title></head>" +
                "<body>" +
                "<h1>❌ Login Error</h1>" +
                "<p>Error: " + e.getMessage() + "</p>" +
                "<a href='/oauth2/authorization/google'>🔄 Try Again</a>" +
                "</body>" +
                "</html>";
        
        response.getWriter().write(errorPage);
    }
}