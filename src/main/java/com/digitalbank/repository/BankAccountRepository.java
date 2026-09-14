package com.digitalbank.repository;

import com.digitalbank.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    long countByCustomerCustomerId(Long customerId);

    List<BankAccount> findByCustomerCustomerId(Long customerId);
}