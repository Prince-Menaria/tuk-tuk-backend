package com.yoyojobcare.auth.kukuapp.ku_ku_app.security;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
                config.setAllowCredentials(false); // ✅ wildcard ke saath false JARURI

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public CorsFilter corsFilter() {
                return new CorsFilter(corsConfigurationSource());
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // ✅ CSRF disable — REST API ke liye MUST hai
                                .csrf(AbstractHttpConfigurer::disable)

                                // ✅ CORS apply
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // ✅ Session stateless — REST API session use nahi karta
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                                .authorizeHttpRequests(auth -> auth
                                                // ✅ Ye sab PUBLIC — koi authentication nahi chahiye
                                                .requestMatchers(
                                                                "/**",
                                                                "/public/**",
                                                                "/oauth2/**",
                                                                "/login/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/api/v1/**" // ✅ Saare API endpoints public
                                                ).permitAll()
                                                .anyRequest().authenticated())
                                                

                                // ✅ Google OAuth2 Login
                                .oauth2Login(oauth -> oauth
                                                .defaultSuccessUrl("/dashboard", true)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2SuccessHandler)
                                                .failureHandler(oAuth2FailureHandler))
                                .logout(AbstractHttpConfigurer::disable);                

                                // .logout(logout -> logout
                                //                 .logoutUrl("/logout") // ← Ye URL hit karo logout ke liye
                                //                 .logoutSuccessUrl("/") // ← Logout ke baad yahan redirect
                                //                 .invalidateHttpSession(true) // ← Session destroy
                                //                 .clearAuthentication(true) // ← Auth clear
                                //                 .deleteCookies("JSESSIONID") // ← Cookie delete
                                // );

                return http.build();
        }
}