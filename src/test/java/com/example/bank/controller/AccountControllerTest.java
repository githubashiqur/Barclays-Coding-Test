package com.example.bank.controller;


import com.example.bank.dto.AccountCreateRequest;
import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.AccountUpdateRequest;
import com.example.bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldReturnCreatedAccount() {
        AccountCreateRequest req = mock(AccountCreateRequest.class);
        AccountResponse resp = mock(AccountResponse.class);
        when(accountService.create(req, authentication)).thenReturn(resp);

        ResponseEntity<AccountResponse> result = accountController.create(req, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(accountService).create(req, authentication);
    }

    @Test
    void list_ShouldReturnListOfAccounts() {
        List<AccountResponse> responses = Arrays.asList(mock(AccountResponse.class),mock(AccountResponse.class));
        when(accountService.list(authentication)).thenReturn(responses);

        ResponseEntity<List<AccountResponse>> result = accountController.list(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responses, result.getBody());
        verify(accountService).list(authentication);
    }

    @Test
    void get_ShouldReturnAccountById() {
        Long accountId = 1L;
        AccountResponse resp = mock(AccountResponse.class);
        when(accountService.get(accountId, authentication)).thenReturn(resp);

        ResponseEntity<AccountResponse> result = accountController.get(accountId, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(accountService).get(accountId, authentication);
    }

    @Test
    void update_ShouldReturnUpdatedAccount() {
        Long accountId = 1L;
        AccountUpdateRequest req = mock(AccountUpdateRequest.class);
        AccountResponse resp = mock(AccountResponse.class);
        when(accountService.update(accountId, req, authentication)).thenReturn(resp);

        ResponseEntity<AccountResponse> result = accountController.update(accountId, req, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(accountService).update(accountId, req, authentication);
    }

    @Test
    void delete_ShouldReturnNoContent() {
        Long accountId = 1L;
        doNothing().when(accountService).delete(accountId, authentication);

        ResponseEntity<Void> result = accountController.delete(accountId, authentication);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(accountService).delete(accountId, authentication);
    }
}