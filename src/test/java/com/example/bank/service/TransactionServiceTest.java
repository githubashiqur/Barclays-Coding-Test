package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.*;
import com.example.bank.exception.*;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class TransactionServiceTest {

    private TransactionRepository txs;
    private AccountRepository accounts;
    private UserRepository users;
    private TransactionService service;
    private Authentication auth;

    private final Long userId = 1L;
    private final Long accountId = 10L;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        txs = mock(TransactionRepository.class);
        accounts = mock(AccountRepository.class);
        users = mock(UserRepository.class);
        auth = mock(Authentication.class);
        service = new TransactionService(txs, accounts, users);

        when(auth.getName()).thenReturn(username);
    }

    private User mockUser() {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        return user;
    }

    private Account mockAccount(BigDecimal balance) {
        Account acc = new Account();
        acc.setId(accountId);
        acc.setBalance(balance);
        User user = mockUser();
        acc.setOwner(user);
        return acc;
    }

    @Test
    void create_deposit_shouldIncreaseBalanceAndSaveTransaction() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("100.00"));
        TransactionCreateRequest req = new TransactionCreateRequest(TransactionType.DEPOSIT, new BigDecimal("50.00"), "Deposit memo");

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));

        TransactionResponse resp = service.create(accountId, req, auth);

        assertEquals(accountId, resp.accountId());
        assertEquals(TransactionType.DEPOSIT, resp.type());
        assertEquals(new BigDecimal("50.00"), resp.amount());
        assertEquals("Deposit memo", resp.memo());

        assertEquals(new BigDecimal("150.00"), acc.getBalance());
        verify(accounts).save(acc);
        verify(txs).save(any(Transaction.class));
    }

    @Test
    void create_withdrawal_shouldDecreaseBalanceAndSaveTransaction() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("200.00"));
        TransactionCreateRequest req = new TransactionCreateRequest(TransactionType.WITHDRAWAL, new BigDecimal("75.00"), "ATM withdrawal");

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));

        TransactionResponse resp = service.create(accountId, req, auth);

        assertEquals(accountId, resp.accountId());
        assertEquals(TransactionType.WITHDRAWAL, resp.type());
        assertEquals(new BigDecimal("75.00"), resp.amount());
        assertEquals("ATM withdrawal", resp.memo());

        assertEquals(new BigDecimal("125.00"), acc.getBalance());
        verify(accounts).save(acc);
        verify(txs).save(any(Transaction.class));
    }

    @Test
    void create_withdrawal_shouldThrowIfInsufficientFunds() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("20.00"));
        TransactionCreateRequest req = new TransactionCreateRequest(TransactionType.WITHDRAWAL, new BigDecimal("50.00"), "Big withdrawal");

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));

        assertThrows(InsufficientFundsException.class, () -> {
            service.create(accountId, req, auth);
        });
        verify(accounts, never()).save(any());
        verify(txs, never()).save(any());
    }

    @Test
    void create_shouldThrowOnInvalidType() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("100.00"));
        TransactionCreateRequest req = new TransactionCreateRequest(null, new BigDecimal("10.00"), "Invalid type");

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));

        assertThrows(BadRequestException.class, () -> {
            service.create(accountId, req, auth);
        });
    }

    @Test
    void list_shouldReturnTransactionsForAccount() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("100.00"));
        Transaction tx1 = Transaction.builder()
                .id(1L)
                .account(acc)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("10.00"))
                .timestamp(OffsetDateTime.now())
                .memo("First")
                .build();
        Transaction tx2 = Transaction.builder()
                .id(2L)
                .account(acc)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("5.00"))
                .timestamp(OffsetDateTime.now())
                .memo("Second")
                .build();

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));
        when(txs.findByAccountIdOrderByTimestampDesc(accountId)).thenReturn(List.of(tx1, tx2));

        List<TransactionResponse> responses = service.list(accountId, auth);

        assertEquals(2, responses.size());
        assertEquals(tx1.getId(), responses.get(0).id());
        assertEquals(tx2.getId(), responses.get(1).id());
    }

    @Test
    void get_shouldReturnTransactionIfExists() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("100.00"));
        Transaction tx = Transaction.builder()
                .id(5L)
                .account(acc)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("20.00"))
                .timestamp(OffsetDateTime.now())
                .memo("Deposit")
                .build();

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));
        when(txs.findByIdAndAccountId(5L, accountId)).thenReturn(Optional.of(tx));

        TransactionResponse resp = service.get(accountId, 5L, auth);

        assertEquals(5L, resp.id());
        assertEquals(accountId, resp.accountId());
        assertEquals(TransactionType.DEPOSIT, resp.type());
        assertEquals(new BigDecimal("20.00"), resp.amount());
        assertEquals("Deposit", resp.memo());
    }

    @Test
    void get_shouldThrowIfTransactionNotFound() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("100.00"));

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));
        when(txs.findByIdAndAccountId(99L, accountId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.get(accountId, 99L, auth);
        });
    }

    @Test
    void getAuthorizedAccount_shouldThrowIfUserNotOwner() {
        User user = mockUser();
        Account acc = mockAccount(new BigDecimal("100.00"));
        User otherUser = new User();
        otherUser.setId(999L);
        acc.setOwner(otherUser);

        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.of(acc));

        assertThrows(ForbiddenException.class, () -> {
            service.list(accountId, auth);
        });
    }

    @Test
    void getAuthorizedAccount_shouldThrowIfUserNotFound() {
        when(users.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.list(accountId, auth);
        });
    }

    @Test
    void getAuthorizedAccount_shouldThrowIfAccountNotFound() {
        User user = mockUser();
        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        when(accounts.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.list(accountId, auth);
        });
    }
}