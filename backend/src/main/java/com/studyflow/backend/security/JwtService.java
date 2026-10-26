package com.studyflow.backend.security;

import com.studyflow.backend.user.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;

    private final long expirationMillis = 15 * 60 * 1000L;

    public JwtService(
            @Value("${JWT_SECRET}") String secret
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET must decode to at least 32 bytes."
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(AppUser user) {

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String expectedEmail) {

        try {
            Claims claims = extractAllClaims(token);

            return expectedEmail.equals(claims.getSubject())
                    && claims.getExpiration().after(new Date());

        } catch (RuntimeException exception) {
            return false;
        }
    }
}