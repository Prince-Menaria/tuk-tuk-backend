// package com.yoyojobcare.auth.kukuapp.ku_ku_app.config;

// import java.io.IOException;
// import java.util.List;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.yoyojobcare.auth.kukuapp.ku_ku_app.security.JwtUtil;
// import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.UserService;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class JwtRequestFilter extends OncePerRequestFilter {

// 	private final JwtUtil jwtUtil;
//     private final UserService userService;

//     public JwtRequestFilter(JwtUtil jwtUtil, UserService userService) {
//         this.jwtUtil = jwtUtil;
//         this.userService = userService;
//     }


//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
//         String authHeader = request.getHeader("Authorization");

//         String path = request.getServletPath();
//         if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         // if (authHeader != null && authHeader.startsWith("Bearer ")) {
//         //     String jwt = authHeader.substring(7);
//         //     String username = jwtUtil.extractUsername(jwt);
//         //     if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//         //         User user = userService.findByEmail(username);
//         //         UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//         //                 user.getEmail(), null, List.of(new SimpleGrantedAuthority(user.getRole()))
//         //         );
//         //         SecurityContextHolder.getContext().setAuthentication(auth);
//         //     }
//         // }

//         filterChain.doFilter(request, response);
//     }

// }
