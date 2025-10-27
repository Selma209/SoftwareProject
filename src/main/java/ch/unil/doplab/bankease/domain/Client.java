package ch.unil.doplab.bankease.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends User {
    private final List<Account> accounts = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private Employee employee;

    public Client(String username, String password, String firstName, String lastName,
                  String email, String phoneNumber) {
        super(username, password, firstName, lastName, email, phoneNumber);
    }

    void setEmployee(Employee employee) { this.employee = employee; }
    public Employee getEmployee() { return employee; }

    public List<Account> getAccounts() { return List.copyOf(accounts); }
    public List<Transaction> getTransactions() { return List.copyOf(transactions); }

    public Account openAccount(AccountType type) {
        Account acc = new Account(this, type);
        accounts.add(acc);
        return acc;
    }

    public BigDecimal getTotalBalance() {
        return accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deposit(Account target, BigDecimal amount, String description) {
        ensureOwns(target);
        Transaction tx = Transaction.createDeposit(target, amount, description);
        target.receiveDeposit(amount);
        transactions.add(tx);
        target.attachTransaction(tx);
    }

    public void withdraw(Account source, BigDecimal amount, String description) {
        ensureOwns(source);
        Transaction tx = Transaction.createWithdrawal(source, amount, description);
        source.processWithdrawal(amount);
        transactions.add(tx);
        source.attachTransaction(tx);
    }

    public Transaction transfer(Account source, Account destination, BigDecimal amount, String description) {
        Objects.requireNonNull(destination, "destination");
        ensureOwns(source);
        boolean needsApproval = amount.compareTo(Employee.APPROVAL_THRESHOLD) > 0;
        Transaction tx = Transaction.createTransfer(source, destination, amount, description,
                needsApproval ? TransactionStatus.PENDING_APPROVAL : TransactionStatus.COMPLETED);
        if (needsApproval) {
            source.attachTransaction(tx);
            destination.attachTransaction(tx);
            transactions.add(tx);
        } else {
            source.processWithdrawal(amount);
            destination.receiveDeposit(amount);
            source.attachTransaction(tx);
            destination.attachTransaction(tx);
            transactions.add(tx);
        }
        return tx;
    }

    public List<Transaction> viewTransactionHistory() { return List.copyOf(transactions); }

    public void requestCancellation(Transaction tx) {
        if (tx.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Only completed transactions can be canceled directly.");
        }
        tx.cancel();
    }

    private void ensureOwns(Account account) {
        if (account == null || !accounts.contains(account)) {
            throw new IllegalArgumentException("Client does not own the specified account.");
        }
    }
}
