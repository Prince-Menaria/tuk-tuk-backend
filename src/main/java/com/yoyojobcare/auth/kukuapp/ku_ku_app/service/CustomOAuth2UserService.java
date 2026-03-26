package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // Load user from Google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract attributes
        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");
        String locale = oAuth2User.getAttribute("locale");

        // Build UserInfo object
        // UserInfo userInfo = UserInfo.builder()
        // .googleId(googleId)
        // .name(name)
        // .email(email)
        // .picture(picture)
        // .locale(locale)
        // .build();

        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setImage(picture);
        User save = userRepository.save(user);
        log.info("✅ User save By Google Login — User: {},  user Id: {}", save, user.getUserId());

        log.info("✅ Google Login Success — User: {}, Email: {}", name, email);

        // 💡 Here you can save/update user in your database
        // userRepository.saveOrUpdate(userInfo);

        return oAuth2User;
    }

}
