package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Provider;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Role;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.RoleRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");
        String locale = oAuth2User.getAttribute("locale");

        log.info("✅ Google OAuth2 User loaded: {} ({})", name, email);

        // Find or create user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, name, picture));

        // Update existing user data if needed
        updateUserIfNeeded(user, name, picture);
        userRepository.save(user);

        log.info("✅ User processed successfully: {}, User ID: {}", user.getEmail(), user.getUserId());

        return oAuth2User;
    }

    private User createNewUser(String email, String name, String picture) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setImage(picture);
        user.setProvider(Provider.GOOGLE);
        user.setEnable(true);

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });

        user.setRoles(Set.of(userRole));
        
        log.info("🆕 Creating new Google user: {}", email);
        return user;
    }

    private void updateUserIfNeeded(User user, String name, String picture) {
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
            log.info("📝 Updating user info for: {}", user.getEmail());
        }
    }
}