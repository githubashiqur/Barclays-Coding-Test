package com.example.bank.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.io.Encoders;

class JwtUtilTest {

    private static String strongBase64Secret() {
        SecretKey key = Jwts.SIG.HS256.key().build();       
        return Encoders.BASE64.encode(key.getEncoded());
    }

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        JwtUtil jwtUtil = new JwtUtil(strongBase64Secret(), "test-issuer", 3600);

        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertEquals(username, jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.isValid(token));
    }

    @Test
    void isValid_shouldReturnFalse_forExpiredToken() {
        JwtUtil jwtUtil = new JwtUtil(strongBase64Secret(), "test-issuer", -1); 

        String token = jwtUtil.generateToken("user");
        assertFalse(jwtUtil.isValid(token), "Token with past expiration should be invalid");
    }

    @Test
    void isValid_shouldReturnFalse_whenVerifiedWithDifferentKey() {
        JwtUtil signer = new JwtUtil(strongBase64Secret(), "issuer", 3600);
        JwtUtil verifierWithDifferentKey = new JwtUtil(strongBase64Secret(), "issuer", 3600);

        String token = signer.generateToken("bob");
        assertFalse(verifierWithDifferentKey.isValid(token),
                "Token signed with a different key should not validate");
    }

}