package com.example.bank.service;

import com.example.bank.dto.AccountCreateRequest;
import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.AccountUpdateRequest;
import com.example.bank.entity.Account;
import com.example.bank.entity.User;
import com.example.bank.exception.ConflictException;
import com.example.bank.exception.ForbiddenException;
import com.example.bank.exception.NotFoundException;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private AccountService accountService;
    private Authentication authentication;
    private User user;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        userRepository = mock(UserRepository.class);
        authentication = mock(Authentication.class);
        accountService = new AccountService(accountRepository, userRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void create_shouldCreateAccount() {
        AccountCreateRequest req = new AccountCreateRequest("12345");
        when(accountRepository.existsByAccountNumber("12345")).thenReturn(false);

        AccountResponse response = accountService.create(req, authentication);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();

        assertEquals("12345", saved.getAccountNumber());
        assertEquals(user, saved.getOwner());
        assertEquals(BigDecimal.ZERO, saved.getBalance());
        assertEquals("12345", response.accountNumber());
        assertEquals(user.getId(), response.ownerId());
    }

    @Test
    void create_shouldThrowConflictIfAccountNumberExists() {
        AccountCreateRequest req = new AccountCreateRequest("12345");
        when(accountRepository.existsByAccountNumber("12345")).thenReturn(true);

        assertThrows(ConflictException.class, () -> accountService.create(req, authentication));
    }

    @Test
    void list_shouldReturnAccountsForUser() {
        Account acc1 = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(user).build();
        Account acc2 = Account.builder().id(2L).accountNumber("A2").balance(BigDecimal.ONE).owner(user).build();
        when(accountRepository.findByOwner(user)).thenReturn(List.of(acc1, acc2));

        List<AccountResponse> responses = accountService.list(authentication);

        assertEquals(2, responses.size());
        assertEquals("A1", responses.get(0).accountNumber());
        assertEquals("A2", responses.get(1).accountNumber());
    }

    @Test
    void get_shouldReturnAccountIfOwner() {
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(user).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        AccountResponse response = accountService.get(1L, authentication);

        assertEquals("A1", response.accountNumber());
        assertEquals(user.getId(), response.ownerId());
    }

    @Test
    void get_shouldThrowNotFoundIfAccountMissing() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.get(1L, authentication));
    }

    @Test
    void get_shouldThrowForbiddenIfNotOwner() {
        User other = new User();
        other.setId(2L);
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(other).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        assertThrows(ForbiddenException.class, () -> accountService.get(1L, authentication));
    }

    @Test
    void update_shouldUpdateAccountNumber() {
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(user).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(accountRepository.existsByAccountNumber("A2")).thenReturn(false);

        AccountUpdateRequest req = new AccountUpdateRequest("A2");
        AccountResponse response = accountService.update(1L, req, authentication);

        assertEquals("A2", acc.getAccountNumber());
        assertEquals("A2", response.accountNumber());
        verify(accountRepository).save(acc);
    }

    @Test
    void update_shouldThrowNotFoundIfAccountMissing() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        AccountUpdateRequest req = new AccountUpdateRequest("A2");

        assertThrows(NotFoundException.class, () -> accountService.update(1L, req, authentication));
    }

    @Test
    void update_shouldThrowForbiddenIfNotOwner() {
        User other = new User();
        other.setId(2L);
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(other).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        AccountUpdateRequest req = new AccountUpdateRequest("A2");

        assertThrows(ForbiddenException.class, () -> accountService.update(1L, req, authentication));
    }

    @Test
    void update_shouldThrowConflictIfAccountNumberExists() {
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(user).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(accountRepository.existsByAccountNumber("A2")).thenReturn(true);

        AccountUpdateRequest req = new AccountUpdateRequest("A2");

        assertThrows(ConflictException.class, () -> accountService.update(1L, req, authentication));
    }

    @Test
    void delete_shouldDeleteIfOwner() {
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(user).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        accountService.delete(1L, authentication);

        verify(accountRepository).delete(acc);
    }

    @Test
    void delete_shouldThrowNotFoundIfAccountMissing() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.delete(1L, authentication));
    }

    @Test
    void delete_shouldThrowForbiddenIfNotOwner() {
        User other = new User();
        other.setId(2L);
        Account acc = Account.builder().id(1L).accountNumber("A1").balance(BigDecimal.TEN).owner(other).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        assertThrows(ForbiddenException.class, () -> accountService.delete(1L, authentication));
    }

    @Test
    void userHasAccounts_shouldReturnTrueIfExists() {
        when(accountRepository.existsByOwnerId(1L)).thenReturn(true);

        assertTrue(accountService.userHasAccounts(1L));
    }

    @Test
    void userHasAccounts_shouldReturnFalseIfNotExists() {
        when(accountRepository.existsByOwnerId(1L)).thenReturn(false);

        assertFalse(accountService.userHasAccounts(1L));
    }

    @Test
    void getAuthUser_shouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.list(authentication));
    }
}