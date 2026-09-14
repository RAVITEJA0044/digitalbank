package com.digitalbank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalbank.entity.BankAccount;
import com.digitalbank.entity.Transaction;
import com.digitalbank.exception.UnauthorizedAccountAccessException;
import com.digitalbank.repository.BankAccountRepository;
import com.digitalbank.repository.TransactionRepository;


@Service
public class TransactionService {

private final TransactionRepository transactionRepository;
private final BankAccountRepository bankAccountRepository;
private final UserService userService;
private final BankAccountService bankAccountService;

public TransactionService(
        TransactionRepository transactionRepository,
        BankAccountRepository bankAccountRepository,
        UserService userService,
        BankAccountService bankAccountService) {

    this.transactionRepository = transactionRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.userService = userService;
    this.bankAccountService = bankAccountService;
}
// =========================================================
// VERIFY ACCOUNT OWNERSHIP
// =========================================================


private void verifyAccountOwnership(Long accountId) {

    String username =
            org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

    com.digitalbank.entity.Customer customer =
            userService.getCustomerByUsername(username);

    boolean belongs =
            bankAccountRepository
                    .findById(accountId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Bank account not found"))
                    .getCustomer()
                    .getCustomerId()
                    .equals(customer.getCustomerId());

    if (!belongs) {
        throw new UnauthorizedAccountAccessException(
                "You are not authorized to access this account");
    }
}
// =========================================================
// DEPOSIT
// =========================================================

@Transactional
public Transaction deposit(
        Long accountId,
        BigDecimal amount,
        String description) {
                verifyAccountOwnership(accountId);

    BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() ->
                    new RuntimeException("Bank account not found"));

    if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
        throw new RuntimeException("Account is not active");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException(
                "Deposit amount must be greater than zero");
    }

    BigDecimal newBalance =
            account.getBalance().add(amount);

    account.setBalance(newBalance);

    bankAccountRepository.save(account);

    Transaction transaction = new Transaction();

    transaction.setTransactionReference(
            UUID.randomUUID().toString());

    transaction.setTransactionType("DEPOSIT");
    transaction.setAmount(amount);
    transaction.setBalanceAfterTransaction(newBalance);
    transaction.setTransactionDate(LocalDateTime.now());
    transaction.setDescription(description);
    transaction.setAccount(account);

    return transactionRepository.save(transaction);
}

// =========================================================
// WITHDRAW
// =========================================================


@Transactional
public Transaction withdraw(
        Long accountId,
        BigDecimal amount,
        String description) {
                verifyAccountOwnership(accountId);

    BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() ->
                    new RuntimeException("Bank account not found"));

    if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
        throw new RuntimeException("Account is not active");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException(
                "Withdrawal amount must be greater than zero");
    }

    if (account.getBalance().compareTo(amount) < 0) {
        throw new RuntimeException("Insufficient balance");
    }

    BigDecimal newBalance =
            account.getBalance().subtract(amount);

    account.setBalance(newBalance);

    bankAccountRepository.save(account);

    Transaction transaction = new Transaction();

    transaction.setTransactionReference(
            UUID.randomUUID().toString());

    transaction.setTransactionType("WITHDRAWAL");
    transaction.setAmount(amount);
    transaction.setBalanceAfterTransaction(newBalance);
    transaction.setTransactionDate(LocalDateTime.now());
    transaction.setDescription(description);
    transaction.setAccount(account);

    return transactionRepository.save(transaction);
}

// =========================================================
// TRANSFER
// =========================================================

@Transactional
public Transaction transfer(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String description) {
                verifyAccountOwnership(fromAccountId);

    if (fromAccountId.equals(toAccountId)) {
        throw new RuntimeException(
                "Cannot transfer to the same account");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException(
                "Transfer amount must be greater than zero");
    }

    BankAccount fromAccount =
            bankAccountRepository.findById(fromAccountId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Source account not found"));

    BankAccount toAccount =
            bankAccountRepository.findById(toAccountId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Destination account not found"));

    if (!"ACTIVE".equalsIgnoreCase(fromAccount.getStatus())) {
        throw new RuntimeException(
                "Source account is not active");
    }

    if (!"ACTIVE".equalsIgnoreCase(toAccount.getStatus())) {
        throw new RuntimeException(
                "Destination account is not active");
    }

    if (fromAccount.getBalance().compareTo(amount) < 0) {
        throw new RuntimeException("Insufficient balance");
    }

    BigDecimal senderNewBalance =
            fromAccount.getBalance().subtract(amount);

    BigDecimal receiverNewBalance =
            toAccount.getBalance().add(amount);

    fromAccount.setBalance(senderNewBalance);
    toAccount.setBalance(receiverNewBalance);

    bankAccountRepository.save(fromAccount);
    bankAccountRepository.save(toAccount);

    String transferReference =
            UUID.randomUUID().toString();

    // =====================================================
    // TRANSFER OUT
    // =====================================================

    Transaction transferOut = new Transaction();

    transferOut.setTransactionReference(
            transferReference);

    transferOut.setTransactionType("TRANSFER_OUT");
    transferOut.setAmount(amount);
    transferOut.setBalanceAfterTransaction(
            senderNewBalance);
    transferOut.setTransactionDate(
            LocalDateTime.now());
    transferOut.setDescription(description);
    transferOut.setAccount(fromAccount);

    transactionRepository.save(transferOut);

    // =====================================================
    // TRANSFER IN
    // =====================================================

    Transaction transferIn = new Transaction();

    transferIn.setTransactionReference(
            transferReference);

    transferIn.setTransactionType("TRANSFER_IN");
    transferIn.setAmount(amount);
    transferIn.setBalanceAfterTransaction(
            receiverNewBalance);
    transferIn.setTransactionDate(
            LocalDateTime.now());
    transferIn.setDescription(description);
    transferIn.setAccount(toAccount);

    transactionRepository.save(transferIn);

    return transferOut;
}

// =========================================================
// GET TRANSACTIONS BY ACCOUNT - PAGINATION
// =========================================================

public Page<Transaction> getTransactionsByAccount(
        Long accountId,
        Pageable pageable) {

    verifyAccountOwnership(accountId);

    return transactionRepository.findByAccountAccountId(
            accountId,
            pageable);
}

// =========================================================
// GET ALL TRANSACTIONS BY ACCOUNT
// =========================================================

public List<Transaction> getTransactionsByAccount(
        Long accountId) {

    verifyAccountOwnership(accountId);

    return transactionRepository
            .findByAccountAccountId(accountId);
}
// =========================================================
// GET TRANSACTIONS BY ACCOUNT + DATE RANGE
// =========================================================

public List<Transaction> getTransactionsByAccountAndDateRange(
        Long accountId,
        LocalDateTime startDate,
        LocalDateTime endDate) {
                
                verifyAccountOwnership(accountId);
    return transactionRepository
            .findByAccountAccountIdAndTransactionDateBetween(
                    accountId,
                    startDate,
                    endDate);
}

// =========================================================
// GET TRANSACTIONS BY TYPE
// =========================================================

public List<Transaction> getTransactionsByType(
        String transactionType) {

    return transactionRepository
            .findByTransactionType(transactionType);
}

// =========================================================
// GET TRANSACTIONS BY ACCOUNT + TYPE
// =========================================================

public List<Transaction> getTransactionsByAccountAndType(
        Long accountId,
        String transactionType) {

    verifyAccountOwnership(accountId);

    String type = transactionType.toUpperCase();

    if (!type.equals("DEPOSIT")
            && !type.equals("WITHDRAWAL")
            && !type.equals("TRANSFER_OUT")
            && !type.equals("TRANSFER_IN")) {

        throw new RuntimeException(
                "Invalid transaction type: " + transactionType);
    }

    return transactionRepository
            .findByAccountAccountIdAndTransactionType(
                    accountId,
                    type);
}

// =========================================================
// GET ALL TRANSACTIONS
// =========================================================

public List<Transaction> getAllTransactions() {

    return transactionRepository.findAll();
}

// =========================================================
// TRANSACTION SUMMARY
// =========================================================

public Map<String, Object> getTransactionSummary(
        Long accountId) {
    verifyAccountOwnership(accountId);
    List<Transaction> transactions =
            transactionRepository
                    .findByAccountAccountId(accountId);

    BigDecimal totalDeposits =
            BigDecimal.ZERO;

    BigDecimal totalWithdrawals =
            BigDecimal.ZERO;

    BigDecimal totalTransfers =
            BigDecimal.ZERO;

    for (Transaction transaction : transactions) {

        String type =
                transaction.getTransactionType();

        BigDecimal amount =
                transaction.getAmount();

        if ("DEPOSIT".equals(type)) {

            totalDeposits =
                    totalDeposits.add(amount);

        } else if ("WITHDRAWAL".equals(type)) {

            totalWithdrawals =
                    totalWithdrawals.add(amount);

        } else if ("TRANSFER".equals(type)
                || "TRANSFER_OUT".equals(type)
                || "TRANSFER_IN".equals(type)) {

            totalTransfers =
                    totalTransfers.add(amount);
        }
    }

    Map<String, Object> summary =
            new LinkedHashMap<>();

    summary.put("accountId", accountId);
    summary.put(
            "totalTransactions",
            transactions.size());
    summary.put(
            "totalDeposits",
            totalDeposits);
    summary.put(
            "totalWithdrawals",
            totalWithdrawals);
    summary.put(
            "totalTransfers",
            totalTransfers);

    return summary;
}

// =========================================================
// VERIFY ACCOUNT BALANCE
// =========================================================

public Map<String, Object> verifyAccountBalance(Long accountId) {
     verifyAccountOwnership(accountId);
    BankAccount account =
            bankAccountRepository.findById(accountId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Bank account not found"));

    List<Transaction> transactions =
            transactionRepository
                    .findByAccountAccountIdOrderByTransactionDateAsc(
                            accountId);

    BigDecimal actualBalance =
            account.getBalance();

    BigDecimal calculatedBalance =
            BigDecimal.ZERO;

    if (!transactions.isEmpty()) {

        /*
         * Transactions are explicitly ordered by
         * transaction date in ascending order.
         *
         * Therefore, the final transaction contains
         * the latest balance after that transaction.
         */
        Transaction latestTransaction =
                transactions.get(transactions.size() - 1);

        calculatedBalance =
                latestTransaction
                        .getBalanceAfterTransaction();
    }

    boolean matches =
            actualBalance.compareTo(calculatedBalance) == 0;

    Map<String, Object> result =
            new LinkedHashMap<>();

    result.put("accountId", accountId);
    result.put("actualBalance", actualBalance);
    result.put("calculatedBalance", calculatedBalance);
    result.put("balanceMatches", matches);

    return result;
}

}
