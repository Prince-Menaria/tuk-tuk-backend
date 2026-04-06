package com.yoyojobcare.auth.kukuapp.ku_ku_app.security;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.handler.OAuth2FailureHandler;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.handler.OAuth2SuccessHandler;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/public/**",
                    "/oauth2/**",
                    "/login/**",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/validate",
                    "/hello",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/ping"
                ).permitAll()

                // ============ WebSocket endpoints (IMPORTANT!) ============
                .requestMatchers(
                    "/ws-chat/**",           // WebSocket endpoint
                    "/ws-chat",              // Direct WebSocket connection
                    "/app/**",               // STOMP application destinations
                    "/topic/**",             // STOMP broker destinations  
                    "/queue/**"              // STOMP user-specific queues

                    "/api/v1/user-profile/save-roles",
                    "/ping"
                ).permitAll()
                
                // Protected endpoints
                .requestMatchers("/api/v1/auth/me").authenticated()
                .requestMatchers("/api/v1/auth/dashboard").authenticated()
                .requestMatchers("/api/v1/user-profile/**").authenticated()
                .requestMatchers("/api/v1/voice-chat/**").authenticated()
                .requestMatchers("/api/v1/chat**").authenticated()
                .requestMatchers("/api/v1/social/follow/**").authenticated()
                .requestMatchers("/api/v1/wallet/**").authenticated()
                .requestMatchers("/dashboard").authenticated()
                
                
                .anyRequest().authenticated())

            // Add JWT filter before OAuth2
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // OAuth2 Login configuration
            .oauth2Login(oauth -> oauth
                // .defaultSuccessUrl("/dashboard", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler));

        return http.build();
    }
}