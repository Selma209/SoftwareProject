package ch.unil.doplab;

import java.time.LocalDateTime;
public class Transaction {

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        AWAITING_APPROVAL,
        CANCELLED
    }

    // --- Attributes ---
    private String transactionId;
    private Account sourceAccount;
    private Account destinationAccount;
    private double amount;
    private LocalDateTime dateTime;
    private TransactionType transactionType;
    private String description;
    private TransactionStatus status;

    // --- Constructor ---
    public Transaction(String transactionId, Account sourceAccount, Account destinationAccount,
                       double amount, TransactionType transactionType, String description) {

        this.transactionId = transactionId;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;

        this.dateTime = LocalDateTime.now(); // Set timestamp to now
        this.status = TransactionStatus.PENDING; // Default status
    }


    public String getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

}