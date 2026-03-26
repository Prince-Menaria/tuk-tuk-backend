// package com.yoyojobcare.auth.kukuapp.ku_ku_app.security;

// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
// import java.util.Date;

// import org.springframework.stereotype.Component;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;

// @Component
// public class JwtUtil {
//     private final String SECRET = "Hb93@#2h0*KDhe!!jjeJJ729!jsA8056Hb93@#2h0*KDhe!!jjeJJ729!jsA8056";

//     public String generateToken(String username) {
//         return Jwts.builder()
//                 .setSubject(username)
//                 .setIssuedAt(new Date())
//                 .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
//                 .signWith(SignatureAlgorithm.HS512, SECRET)
//                 .compact();
//     }

//     public String extractUsername(String token) {
//         return Jwts.parser().setSigningKey(SECRET)
//                 .parseClaimsJws(token).getBody().getSubject();
//     }

// }
