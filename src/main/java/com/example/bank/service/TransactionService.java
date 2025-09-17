package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.*;
import com.example.bank.exception.*;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository txs;
    private final AccountRepository accounts;
    private final UserRepository users;

    public TransactionResponse create(Long accountId, TransactionCreateRequest req, Authentication auth) {
        Account acc = getAuthorizedAccount(accountId, auth);

        if (req.type() == TransactionType.WITHDRAWAL) {
            if (acc.getBalance().compareTo(req.amount()) < 0)
                throw new InsufficientFundsException("Insufficient funds");
            acc.setBalance(acc.getBalance().subtract(req.amount()));
        } else if (req.type() == TransactionType.DEPOSIT) {
            acc.setBalance(acc.getBalance().add(req.amount()));
        } else {
            throw new BadRequestException("Invalid transaction type");
        }

        Transaction tx = Transaction.builder()
                .account(acc)
                .amount(req.amount().setScale(2, RoundingMode.HALF_UP))
                .type(req.type())
                .balance(acc.getBalance().setScale(2, RoundingMode.HALF_UP))
                .timestamp(OffsetDateTime.now())
                .memo(req.memo())
                .build();
        accounts.save(acc); 
        txs.save(tx);
        return toResponse(tx);
    }

    public List<TransactionResponse> list(Long accountId, Authentication auth) {
        Account acc = getAuthorizedAccount(accountId, auth);
        return txs.findByAccountIdOrderByTimestampDesc(acc.getId()).stream().map(this::toResponse).toList();
    }

    public TransactionResponse get(Long accountId, Long txId, Authentication auth) {
        Account acc = getAuthorizedAccount(accountId, auth);
        Transaction tx = txs.findByIdAndAccountId(txId, acc.getId())
                .orElseThrow(() -> new NotFoundException("Transaction not found for this account"));
        return toResponse(tx);
    }

    private Account getAuthorizedAccount(Long accountId, Authentication auth) {
        User user = users.findByUsername(auth.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
        Account acc = accounts.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (!acc.getOwner().getId().equals(user.getId()))
            throw new ForbiddenException("Cannot access another user's account");
        return acc;
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getAccount().getId(), t.getType(), t.getBalance(), t.getAmount(), t.getTimestamp(), t.getMemo()
        );
    }
}
