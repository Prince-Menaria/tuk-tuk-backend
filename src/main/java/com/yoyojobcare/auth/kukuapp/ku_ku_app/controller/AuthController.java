package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AuthController {

    // PRIVATE API (login required)
    @GetMapping("/dashboard")
    public AuthResponse dashboard(@AuthenticationPrincipal OAuth2User user) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setEmail(user.getAttribute("email"));
        authResponse.setName(user.getAttribute("name"));
        authResponse.setPicture(user.getAttribute("picture"));
        return authResponse;
    }

    // USER DETAILS
    @GetMapping("/user")
    public Object user(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttributes();
    }

    @GetMapping("/hello")
    public String  hello() {
        return "Hello World";
    }

}

@Data
class AuthResponse{

    private String name;
    private String email;
    @Column(length = 1000)
    private String picture;
}
