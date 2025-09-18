package com.example.bank.service;

import com.example.bank.dto.AuthRequest;
import com.example.bank.dto.AuthResponse;
import com.example.bank.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(authenticationManager, jwtUtil);
    }

    @Test
    void login_shouldAuthenticateAndReturnAuthResponse() {
        String username = "user1";
        String password = "pass123";
        String token = "jwt-token";
        AuthRequest authRequest = new AuthRequest(username, password);

        when(jwtUtil.generateToken(username)).thenReturn(token);

        AuthResponse response = authService.login(authRequest);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(jwtUtil).generateToken(username);

        assertNotNull(response);
        assertEquals(token, response.token());
    }

    @Test
    void login_shouldThrowException_whenAuthenticationFails() {
        String username = "user2";
        String password = "wrongpass";
        AuthRequest authRequest = new AuthRequest(username, password);

        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> authService.login(authRequest));
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verifyNoInteractions(jwtUtil);
    }
}