package ch.unil.doplab.bankease.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final Account source;        // null for DEPOSIT
    private final Account destination;   // null for WITHDRAWAL
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final String description;
    private TransactionStatus status;

    private Transaction(Account source, Account destination, BigDecimal amount,
                        TransactionType type, String description, TransactionStatus status) {
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.destination = destination;
        this.amount = Objects.requireNonNull(amount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be > 0.");
        this.type = Objects.requireNonNull(type);
        this.description = description == null ? "" : description;
        this.status = Objects.requireNonNull(status);
        this.timestamp = LocalDateTime.now();
    }

    public static Transaction createDeposit(Account destination, BigDecimal amount, String description) {
        Objects.requireNonNull(destination, "destination");
        return new Transaction(null, destination, amount, TransactionType.DEPOSIT, description, TransactionStatus.COMPLETED);
    }

    public static Transaction createWithdrawal(Account source, BigDecimal amount, String description) {
        Objects.requireNonNull(source, "source");
        return new Transaction(source, null, amount, TransactionType.WITHDRAWAL, description, TransactionStatus.COMPLETED);
    }

    public static Transaction createTransfer(Account source, Account destination, BigDecimal amount,
                                             String description, TransactionStatus initialStatus) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        if (source == destination) throw new IllegalArgumentException("Source and destination must differ.");
        return new Transaction(source, destination, amount, TransactionType.TRANSFER, description, initialStatus);
    }

    void markApproved() {
        if (status != TransactionStatus.PENDING_APPROVAL) throw new IllegalStateException("Transaction not pending.");
        status = TransactionStatus.APPROVED;
    }

    void markRejected() {
        if (status != TransactionStatus.PENDING_APPROVAL) throw new IllegalStateException("Transaction not pending.");
        status = TransactionStatus.REJECTED;
    }

    void applyEffects() {
        if (type != TransactionType.TRANSFER) return;
        if (status != TransactionStatus.APPROVED) throw new IllegalStateException("Cannot apply effects unless approved.");
        source.processWithdrawal(amount);
        destination.receiveDeposit(amount);
        status = TransactionStatus.COMPLETED;
    }

    public void cancel() {
        switch (type) {
            case DEPOSIT -> { destination.processWithdrawal(amount); status = TransactionStatus.CANCELED; }
            case WITHDRAWAL -> { source.receiveDeposit(amount); status = TransactionStatus.CANCELED; }
            case TRANSFER -> {
                if (status != TransactionStatus.COMPLETED)
                    throw new IllegalStateException("Only completed transfers can be canceled directly.");
                destination.processWithdrawal(amount);
                source.receiveDeposit(amount);
                status = TransactionStatus.CANCELED;
            }
        }
    }

    public String getId() { return id; }
    public Account getSource() { return source; }
    public Account getDestination() { return destination; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionType getType() { return type; }
    public String getDescription() { return description; }
    public TransactionStatus getStatus() { return status; }
}
