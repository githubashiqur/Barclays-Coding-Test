package com.example.bank.repository;

import com.example.bank.entity.User;
import com.example.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwner(User owner);
    Optional<Account> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerId(Long ownerId);
    boolean existsByAccountNumber(String accountNumber);
}

