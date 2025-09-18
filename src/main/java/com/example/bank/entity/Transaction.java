package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="transactions")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=12)
    private TransactionType type; 

    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal amount;

    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal balance; 

    @Column(nullable=false)
    private OffsetDateTime timestamp;

    @Column(length=140)
    private String memo;
}

