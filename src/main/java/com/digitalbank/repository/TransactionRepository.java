package com.digitalbank.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalbank.entity.Transaction;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountAccountId(
            Long accountId);

    List<Transaction> findByAccountAccountIdOrderByTransactionDateAsc(
            Long accountId);

    List<Transaction> findByTransactionType(
            String transactionType);

    List<Transaction> findByAccountAccountIdAndTransactionType(
            Long accountId,
            String transactionType);

    List<Transaction>
    findByAccountAccountIdAndTransactionDateBetween(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    Page<Transaction> findByAccountAccountId(
            Long accountId,
            Pageable pageable);
}