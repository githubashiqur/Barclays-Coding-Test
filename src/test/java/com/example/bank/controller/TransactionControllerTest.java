package com.example.bank.controller;

import com.example.bank.dto.TransactionCreateRequest;
import com.example.bank.dto.TransactionResponse;
import com.example.bank.service.TransactionService;
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



class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldReturnCreatedResponse() {
        Long accountId = 1L;
        TransactionCreateRequest req = mock(TransactionCreateRequest.class);
        TransactionResponse resp = mock(TransactionResponse.class);

        when(transactionService.create(accountId, req, authentication)).thenReturn(resp);

        ResponseEntity<TransactionResponse> result = controller.create(accountId, req, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(transactionService).create(accountId, req, authentication);
    }

    @Test
    void list_ShouldReturnListOfTransactions() {
        Long accountId = 2L;
        List<TransactionResponse> responses = Arrays.asList(mock( TransactionResponse.class), mock( TransactionResponse.class));

        when(transactionService.list(accountId, authentication)).thenReturn(responses);

        ResponseEntity<List<TransactionResponse>> result = controller.list(accountId, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responses, result.getBody());
        verify(transactionService).list(accountId, authentication);
    }

    @Test
    void get_ShouldReturnTransactionResponse() {
        Long accountId = 3L;
        Long transactionId = 10L;
        TransactionResponse resp = mock(TransactionResponse.class);

        when(transactionService.get(accountId, transactionId, authentication)).thenReturn(resp);

        ResponseEntity<TransactionResponse> result = controller.get(accountId, transactionId, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
        verify(transactionService).get(accountId, transactionId, authentication);
    }
}