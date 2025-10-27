package ch.unil.doplab.bankease.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Account {
    private final String accountNumber;
    private final Client owner;
    private final AccountType type;
    private BigDecimal balance;
    private final LocalDate openingDate;
    private final List<Transaction> relatedTransactions = new ArrayList<>();

    public Account(Client owner, AccountType type) {
        this.accountNumber = UUID.randomUUID().toString();
        this.owner = Objects.requireNonNull(owner);
        this.type = Objects.requireNonNull(type);
        this.balance = BigDecimal.ZERO;
        this.openingDate = LocalDate.now();
    }

    void receiveDeposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    void processWithdrawal(BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient funds.");
        balance = balance.subtract(amount);
    }

    void attachTransaction(Transaction tx) { relatedTransactions.add(tx); }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be > 0.");
    }

    public String getAccountNumber() { return accountNumber; }
    public Client getOwner() { return owner; }
    public AccountType getType() { return type; }
    public BigDecimal getBalance() { return balance; }
    public LocalDate getOpeningDate() { return openingDate; }
    public List<Transaction> getRelatedTransactions() { return List.copyOf(relatedTransactions); }
}
