package com.digitalbank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbank.entity.BankAccount;
import com.digitalbank.service.BankAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public BankAccount createAccount(@Valid @RequestBody BankAccount bankAccount) {
        return bankAccountService.createAccount(bankAccount);
    }

    @GetMapping
    public List<BankAccount> getAllAccounts() {
        return bankAccountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public BankAccount getAccountById(@PathVariable Long id) {
        return bankAccountService.getAccountById(id);
    }

    @GetMapping("/number/{accountNumber}")
    public BankAccount getAccountByNumber(@PathVariable String accountNumber) {
        return bankAccountService.getAccountByNumber(accountNumber);
    }

    @GetMapping("/customer/{customerId}")
    public List<BankAccount> getAccountsByCustomer(
            @PathVariable Long customerId) {

        return bankAccountService.getAccountsByCustomer(customerId);

    }

    @PutMapping("/{id}/status")
    public BankAccount updateAccountStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return bankAccountService.updateAccountStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return "Bank account deleted successfully";
    }
}
