package ch.unil.doplab;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {

    // --- Attributes ---
    private List<Account> accounts;
    private List<Transaction> transactionHistory;

    // --- Constructor ---
    public Client(
            String userId, String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        super(userId, username, password, firstName, lastName, email, phoneNumber);
        this.accounts = new ArrayList<>();
        this.transactionHistory = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

}