
package com.digitalbank.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbank.entity.Transaction;
import com.digitalbank.exception.UnauthorizedAccountAccessException;
import com.digitalbank.service.BankAccountService;
import com.digitalbank.service.TransactionService;
import com.digitalbank.service.UserService;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

  private final TransactionService transactionService;
private final BankAccountService bankAccountService;
private final UserService userService;

  public TransactionController(
        TransactionService transactionService,
        BankAccountService bankAccountService,
        UserService userService) {

    this.transactionService = transactionService;
    this.bankAccountService = bankAccountService;
    this.userService = userService;
}
    // =========================
    // DEPOSIT
    // =========================

    @PostMapping("/deposit/{accountId}")
    public ResponseEntity<Transaction> deposit(
            @PathVariable Long accountId,

            @RequestParam
            @Positive(
                message = "Deposit amount must be greater than zero"
            )
            BigDecimal amount,

            @RequestParam(required = false)
            String description) {

        Transaction transaction =
                transactionService.deposit(
                        accountId,
                        amount,
                        description
                );

        return ResponseEntity.ok(transaction);
    }

    // =========================
    // WITHDRAW
    // =========================

    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<Transaction> withdraw(
            @PathVariable Long accountId,

            @RequestParam
            @Positive(
                message = "Withdrawal amount must be greater than zero"
            )
            BigDecimal amount,

            @RequestParam(required = false)
            String description) {

        Transaction transaction =
                transactionService.withdraw(
                        accountId,
                        amount,
                        description
                );

        return ResponseEntity.ok(transaction);
    }

    // =========================
    // TRANSFER
    // =========================

  @PostMapping("/transfer")
public ResponseEntity<Transaction> transfer(
        @RequestParam Long fromAccountId,

        @RequestParam String toAccountNumber,

        @RequestParam
        @Positive(
            message = "Transfer amount must be greater than zero"
        )
        BigDecimal amount,

        @RequestParam(required = false)
        String description) {

    Long toAccountId =
            bankAccountService
                    .getAccountByNumber(toAccountNumber)
                    .getAccountId();

    Transaction transaction =
            transactionService.transfer(
                    fromAccountId,
                    toAccountId,
                    amount,
                    description
            );

    return ResponseEntity.ok(transaction);
}
    // =========================
    // TRANSACTIONS BY ACCOUNT
    // =========================
@GetMapping("/account/{accountId}")
public ResponseEntity<List<Transaction>> getTransactionsByAccount(
        @PathVariable Long accountId,
        org.springframework.security.core.Authentication authentication) {

    verifyAccountOwnership(
            accountId,
            authentication.getName()
    );

    return ResponseEntity.ok(
            transactionService.getTransactionsByAccount(accountId)
    );
}

    // =========================
    // PAGINATED TRANSACTIONS
    // =========================

    @GetMapping("/account/{accountId}/page")
    public ResponseEntity<Page<Transaction>> getTransactionsByAccountPaged(
            @PathVariable Long accountId,
            Pageable pageable) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccount(
                        accountId,
                        pageable
                )
        );
    }

    // =========================
    // TRANSACTIONS BY DATE RANGE
    // =========================

    @GetMapping("/account/{accountId}/date-range")
    public ResponseEntity<List<Transaction>>
    getTransactionsByAccountAndDateRange(
            @PathVariable Long accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDateTime start =
                LocalDateTime.parse(startDate);

        LocalDateTime end =
                LocalDateTime.parse(endDate);

        return ResponseEntity.ok(
                transactionService
                        .getTransactionsByAccountAndDateRange(
                                accountId,
                                start,
                                end
                        )
        );
    }

    // =========================
    // TRANSACTIONS BY ACCOUNT + TYPE
    // =========================

    @GetMapping("/account/{accountId}/type/{transactionType}")
    public ResponseEntity<List<Transaction>>
    getTransactionsByAccountAndType(
            @PathVariable Long accountId,
            @PathVariable String transactionType) {

        return ResponseEntity.ok(
                transactionService
                        .getTransactionsByAccountAndType(
                                accountId,
                                transactionType
                        )
        );
    }

    // =========================
    // TRANSACTIONS BY TYPE
    // =========================

    @GetMapping("/type/{transactionType}")
    public ResponseEntity<List<Transaction>>
    getTransactionsByType(
            @PathVariable String transactionType) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByType(
                        transactionType
                )
        );
    }

    // =========================
    // ALL TRANSACTIONS
    // =========================

    @GetMapping
    public ResponseEntity<List<Transaction>>
    getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    // =========================
    // TRANSACTION SUMMARY
    // =========================

    @GetMapping("/account/{accountId}/summary")
    public ResponseEntity<Map<String, Object>>
    getTransactionSummary(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                transactionService.getTransactionSummary(accountId)
        );
    }

    // =========================
    // VERIFY ACCOUNT BALANCE
    // =========================

    @GetMapping("/account/{accountId}/verify-balance")
    public ResponseEntity<Map<String, Object>>
    verifyAccountBalance(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                transactionService.verifyAccountBalance(accountId)
        );
    }

    private void verifyAccountOwnership(
        Long accountId,
        String username) {

    Long customerId =
            userService
                    .getCustomerByUsername(username)
                    .getCustomerId();

    boolean belongs =
            bankAccountService
                    .belongsToCustomer(
                            accountId,
                            customerId
                    );

   if (!belongs) {
    throw new UnauthorizedAccountAccessException(
            "You are not authorized to access this account");
}
}
}

