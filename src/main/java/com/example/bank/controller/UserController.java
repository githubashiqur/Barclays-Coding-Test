package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.service.AccountService;
import com.example.bank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService users;
    private final AccountService accounts;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest req) {
        UserResponse resp = users.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> get(@PathVariable Long userId, Authentication auth) {
        return ResponseEntity.ok(users.get(userId, auth));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> update(@PathVariable Long userId,
                                               @Valid @RequestBody UserUpdateRequest req,
                                               Authentication auth) {
        return ResponseEntity.ok(users.update(userId, req, auth));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, Authentication auth) {
        boolean hasAccounts = accounts.userHasAccounts(userId);
        users.delete(userId, hasAccounts, auth);
        return ResponseEntity.noContent().build();
    }
}
