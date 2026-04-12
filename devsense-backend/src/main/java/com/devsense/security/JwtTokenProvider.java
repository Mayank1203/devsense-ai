package com.devsense.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    // @Component = Spring manages this as a bean (can be @Autowired elsewhere)

    @Value("${jwt.secret}")
    // @Value reads from application.yml: jwt.secret
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        // Decode the base64 secret and create an HMAC key for signing
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String email) {
        // Build and sign the JWT
        return Jwts.builder()
                .subject(email)                          // 'sub' claim = user identity
                .issuedAt(new Date())                    // 'iat' = when token was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key())                         // signs with HMAC-SHA256
                .compact();                              // produces the token string
        // Result looks like: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4LmNvbSJ9.xxxx
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(key())             // validates signature
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();                 // returns the email we put in 'sub'
    }

    public boolean isValid(String token) {
        try {
            extractEmail(token);   // if this throws, token is invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;          // expired, tampered, or malformed
        }
    }
}

