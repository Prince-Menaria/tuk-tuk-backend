package com.yoyojobcare.auth.kukuapp.ku_ku_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                if (userId != null) {
                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null && user.isEnable()) {
                        Claims claims = jwtTokenProvider.getClaimsFromToken(jwt);
                        @SuppressWarnings("unchecked")
                        List<String> roles = (List<String>) claims.get("roles");
                        
                        List<SimpleGrantedAuthority> authorities = roles != null ? 
                            roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList()) : 
                            List.of(new SimpleGrantedAuthority("ROLE_USER"));

                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("✅ JWT Authentication successful for user: {}", user.getEmail());
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Cannot set user authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        return path.startsWith("/public/") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/login/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/api/v1/auth/refresh") ||
               path.equals("/api/v1/auth/validate") ||
               path.equals("/hello");
    }
}