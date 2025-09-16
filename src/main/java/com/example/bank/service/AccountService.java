package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.Account;
import com.example.bank.entity.User;
import com.example.bank.exception.*;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accounts;
    private final UserRepository users;

    public AccountResponse create(AccountCreateRequest req, Authentication auth) {
        User owner = getAuthUser(auth);
        if (accounts.existsByAccountNumber(req.accountNumber()))
            throw new ConflictException("Account number already exists");

        Account acc = Account.builder()
                .accountNumber(req.accountNumber())
                .owner(owner)
                .balance(BigDecimal.ZERO)
                .build();
        accounts.save(acc);
        return toResponse(acc);
    }

    public List<AccountResponse> list(Authentication auth) {
        User owner = getAuthUser(auth);
        return accounts.findByOwner(owner).stream().map(this::toResponse).toList();
    }

    public AccountResponse get(Long id, Authentication auth) {
        User owner = getAuthUser(auth);
        Account acc = accounts.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
        if (!acc.getOwner().getId().equals(owner.getId()))
            throw new ForbiddenException("Cannot access another user's account");
        return toResponse(acc);
    }

    public AccountResponse update(Long id, AccountUpdateRequest req, Authentication auth) {
        User owner = getAuthUser(auth);
        Account acc = accounts.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
        if (!acc.getOwner().getId().equals(owner.getId()))
            throw new ForbiddenException("Cannot update another user's account");
        if (req.accountNumber() != null && !req.accountNumber().isBlank()) {
            if (!req.accountNumber().equals(acc.getAccountNumber())
                    && accounts.existsByAccountNumber(req.accountNumber()))
                throw new ConflictException("Account number already exists");
            acc.setAccountNumber(req.accountNumber());
        }
        accounts.save(acc);
        return toResponse(acc);
    }

    public void delete(Long id, Authentication auth) {
        User owner = getAuthUser(auth);
        Account acc = accounts.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
        if (!acc.getOwner().getId().equals(owner.getId()))
            throw new ForbiddenException("Cannot delete another user's account");
        accounts.delete(acc);
    }

    public boolean userHasAccounts(Long userId) {
        return accounts.existsByOwnerId(userId);
    }

    private User getAuthUser(Authentication auth) {
        User u = users.findByUsername(auth.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
        return u;
    }

    private AccountResponse toResponse(Account a) {
        return new AccountResponse(a.getId(), a.getAccountNumber(), a.getBalance(), a.getOwner().getId());
    }
}
