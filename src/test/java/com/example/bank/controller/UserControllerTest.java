package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.service.AccountService;
import com.example.bank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldReturnCreatedUser() {
        UserCreateRequest req = mock(UserCreateRequest.class);
        UserResponse resp = mock(UserResponse.class);
        when(userService.create(req)).thenReturn(resp);

        ResponseEntity<UserResponse> result = userController.create(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(userService).create(req);
    }

    @Test
    void get_ShouldReturnUser() {
        Long userId = 1L;
        UserResponse resp = mock( UserResponse.class);
        when(userService.get(userId, authentication)).thenReturn(resp);

        ResponseEntity<UserResponse> result = userController.get(userId, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(userService).get(userId, authentication);
    }

    @Test
    void update_ShouldReturnUpdatedUser() {
        Long userId = 2L;
        UserUpdateRequest req = mock( UserUpdateRequest.class);
        UserResponse resp = mock(UserResponse.class);
        when(userService.update(userId, req, authentication)).thenReturn(resp);

        ResponseEntity<UserResponse> result = userController.update(userId, req, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(userService).update(userId, req, authentication);
    }

    @Test
    void delete_ShouldDeleteUserAndReturnNoContent() {
        Long userId = 3L;
        when(accountService.userHasAccounts(userId)).thenReturn(false);

        ResponseEntity<Void> result = userController.delete(userId, authentication);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(accountService).userHasAccounts(userId);
        verify(userService).delete(userId, false, authentication);
    }

    @Test
    void delete_ShouldDeleteUserWithAccountsAndReturnNoContent() {
        Long userId = 4L;
        when(accountService.userHasAccounts(userId)).thenReturn(true);

        ResponseEntity<Void> result = userController.delete(userId, authentication);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(accountService).userHasAccounts(userId);
        verify(userService).delete(userId, true, authentication);
    }
}