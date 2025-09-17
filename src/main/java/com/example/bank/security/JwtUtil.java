package com.example.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final String issuer;
    private final long expirySeconds;

    public JwtUtil(
            @Value("${eaglebank.jwt.secret}") String secret,
            @Value("${eaglebank.jwt.issuer:eaglebank}") String issuer,
            @Value("${eaglebank.jwt.expirySeconds:86400}") long expirySeconds
    ) {
        SecretKey k;
        try {
            byte[] bytes = Decoders.BASE64.decode(secret);
            k = Keys.hmacShaKeyFor(bytes);
        } catch (IllegalArgumentException ignored) {
            k = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        this.key = k;
        this.issuer = issuer;
        this.expirySeconds = expirySeconds;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirySeconds)))
                .signWith(key) // 0.13 infers HS algorithm from key
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)      // replaces setSigningKey / parserBuilder in 0.13
                .build()
                .parseSignedClaims(token);
    }
}