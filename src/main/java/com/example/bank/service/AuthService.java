package com.example.bank.service;

import com.example.bank.dto.AuthRequest;
import com.example.bank.dto.AuthResponse;
import com.example.bank.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    // Authentication service which validates credentials and issues JWT tokens.

    private final AuthenticationManager authManager;
    private final JwtUtil jwt;

    public AuthResponse login(AuthRequest req) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.username(), req.password());
        authManager.authenticate(authToken);
        return new AuthResponse(jwt.generateToken(req.username()));
    }
}
