package ch.unil.doplab;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Account {

    public enum AccountType {
        CURRENT,
        SAVINGS,
        BUSINESS
    }
    // --- Attributes ---
    private String accountNumber;
    private AccountType accountType;
    private double balance;
    private LocalDate openingDate;
    private List<Transaction> transactions;

    // --- Constructor ---
    public Account(String accountNumber, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = 0.0;
        this.openingDate = LocalDate.now();
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

}