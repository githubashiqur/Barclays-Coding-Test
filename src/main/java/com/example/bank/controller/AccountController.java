package com.example.bank.controller;

import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.AccountCreateRequest;
import com.example.bank.dto.AccountUpdateRequest;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    // REST controller for bank account operations.

    private final AccountService accounts;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountCreateRequest req, Authentication auth) {
        AccountResponse resp = accounts.create(req, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> list(Authentication auth) {
        return ResponseEntity.ok(accounts.list(auth));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> get(@PathVariable Long accountId, Authentication auth) {
        return ResponseEntity.ok(accounts.get(accountId, auth));
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountResponse> update(@PathVariable Long accountId,
                                                  @Valid @RequestBody AccountUpdateRequest req,
                                                  Authentication auth) {
        return ResponseEntity.ok(accounts.update(accountId, req, auth));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(@PathVariable Long accountId, Authentication auth) {
        accounts.delete(accountId, auth);
        return ResponseEntity.noContent().build();
    }
}
