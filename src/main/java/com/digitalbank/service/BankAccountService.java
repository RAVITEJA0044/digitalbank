package com.digitalbank.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.digitalbank.entity.BankAccount;
import com.digitalbank.entity.Customer;
import com.digitalbank.repository.BankAccountRepository;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerService customerService;

    public BankAccountService(
            BankAccountRepository bankAccountRepository,
            CustomerService customerService) {

        this.bankAccountRepository = bankAccountRepository;
        this.customerService = customerService;
    }

    public BankAccount createAccount(BankAccount bankAccount) {

// Validate customer
        if (bankAccount.getCustomer() == null
                || bankAccount.getCustomer().getCustomerId() == null) {

            throw new RuntimeException(
                    "Customer information is required");
        }

        Long customerId
                = bankAccount.getCustomer().getCustomerId();

        Customer customer
                = customerService.getCustomerById(customerId);

// Validate account number
        if (bankAccount.getAccountNumber() == null
                || bankAccount.getAccountNumber().isBlank()) {

            throw new RuntimeException(
                    "Account number is required");
        }

        if (bankAccountRepository
                .findByAccountNumber(
                        bankAccount.getAccountNumber())
                .isPresent()) {

            throw new RuntimeException(
                    "Account number already exists: "
                    + bankAccount.getAccountNumber());
        }

// Validate account type
// Validate account type
        if (bankAccount.getAccountType() == null
                || bankAccount.getAccountType().isBlank()) {

            throw new RuntimeException(
                    "Account type is required");

        }

        String accountType
                = bankAccount.getAccountType().toUpperCase();

        if (!accountType.equals("SAVINGS")
                && !accountType.equals("CURRENT")) {

            throw new RuntimeException(
                    "Invalid account type. Allowed types: SAVINGS, CURRENT");

        }

        bankAccount.setAccountType(accountType);

// Validate balance
        if (bankAccount.getBalance() == null) {

            bankAccount.setBalance(BigDecimal.ZERO);

        } else if (bankAccount.getBalance()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Initial balance cannot be negative");
        }

// Set customer from database
        bankAccount.setCustomer(customer);

// Set default status
        if (bankAccount.getStatus() == null
                || bankAccount.getStatus().isBlank()) {

            bankAccount.setStatus("ACTIVE");
        }

        return bankAccountRepository.save(bankAccount);

    }

    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    public BankAccount getAccountById(Long id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
    }

    public boolean belongsToCustomer(Long accountId, Long customerId) {

        BankAccount account = getAccountById(accountId);

        return account.getCustomer()
                .getCustomerId()
                .equals(customerId);
    }

    public BankAccount getAccountByNumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
    }

    public List<BankAccount> getAccountsByCustomer(Long customerId) {

// Make sure the customer exists
        customerService.getCustomerById(customerId);

        return bankAccountRepository
                .findByCustomerCustomerId(customerId);

    }

    public BankAccount updateAccountStatus(Long id, String status) {

        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (!status.equalsIgnoreCase("ACTIVE")
                && !status.equalsIgnoreCase("INACTIVE")) {
            throw new RuntimeException("Invalid account status");
        }

        account.setStatus(status.toUpperCase());

        return bankAccountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        bankAccountRepository.deleteById(id);
    }
}
