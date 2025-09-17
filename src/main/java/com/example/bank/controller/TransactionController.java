package com.example.bank.controller;

import com.example.bank.service.TransactionService;
import com.example.bank.dto.TransactionCreateRequest;
import com.example.bank.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts/{accountId}/transactions")
@RequiredArgsConstructor
public class TransactionController {
    // REST controller for transaction operations on an account.

    private final TransactionService txs;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@PathVariable Long accountId,
                                                      @Valid @RequestBody TransactionCreateRequest req,
                                                      Authentication auth) {
        TransactionResponse resp = txs.create(accountId, req, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(@PathVariable Long accountId, Authentication auth) {
        return ResponseEntity.ok(txs.list(accountId, auth));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> get(@PathVariable Long accountId,
                                                   @PathVariable Long transactionId,
                                                   Authentication auth) {
        return ResponseEntity.ok(txs.get(accountId, transactionId, auth));
    }
}
