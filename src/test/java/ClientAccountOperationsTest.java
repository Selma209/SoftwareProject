import ch.unil.doplab.bankease.domain.Account;
import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.domain.AccountType;
import ch.unil.doplab.bankease.domain.TransactionType;
import ch.unil.doplab.bankease.domain.Transaction;
import ch.unil.doplab.bankease.domain.TransactionStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ClientAccountOperationsTest {

    private Client client;
    private Account accCurrent;
    private Account accSavings;

    @BeforeEach
    void setUp() {
        client = new Client("alice", "pw", "Alice", "Smith", "a@x.y", "000");
        accCurrent = client.openAccount(AccountType.CURRENT);
        accSavings = client.openAccount(AccountType.SAVINGS);
    }

    @Test
    void openAccount_initialBalanceIsZero_andAccountsAreOwned() {
        assertEquals(BigDecimal.ZERO, accCurrent.getBalance());
        assertEquals(client, accCurrent.getOwner());
        assertEquals(2, client.getAccounts().size());
    }

    @Test
    void deposit_increasesBalance_andAddsTransaction() {
        client.deposit(accCurrent, new BigDecimal("150.00"), "initial deposit");
        assertEquals(new BigDecimal("150.00"), accCurrent.getBalance());
        assertEquals(1, client.getTransactions().size());
        Transaction tx = client.getTransactions().get(0);
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertNull(tx.getSource());
        assertEquals(accCurrent, tx.getDestination());
        assertEquals(new BigDecimal("150.00"), tx.getAmount());
    }

    @Test
    void withdraw_decreasesBalance_andAddsTransaction() {
        client.deposit(accCurrent, new BigDecimal("200.00"), "seed");
        client.withdraw(accCurrent, new BigDecimal("50.00"), "atm");
        assertEquals(new BigDecimal("150.00"), accCurrent.getBalance());
        assertEquals(2, client.getTransactions().size());
        Transaction tx = client.getTransactions().get(1);
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertEquals(accCurrent, tx.getSource());
        assertNull(tx.getDestination());
        assertEquals(new BigDecimal("50.00"), tx.getAmount());
    }

    @Test
    void withdraw_fails_whenInsufficientFunds() {
        // aucun dépôt -> solde = 0
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> client.withdraw(accCurrent, new BigDecimal("1.00"), "oops"));
        assertEquals("Insufficient funds.", ex.getMessage());
    }

    @Test
    void totalBalance_aggregatesAllAccounts() {
        client.deposit(accCurrent, new BigDecimal("120.00"), "seed1");
        client.deposit(accSavings, new BigDecimal("80.00"), "seed2");
        assertEquals(new BigDecimal("200.00"), client.getTotalBalance());
    }

    @Test
    void deposit_fails_onNonOwnedAccount() {
        Client bob = new Client("bob","pw","Bob","B","b@x.y","111");
        Account bobsAcc = bob.openAccount(AccountType.CURRENT);
        assertThrows(IllegalArgumentException.class,
                () -> client.deposit(bobsAcc, new BigDecimal("10.00"), "not owner"));
    }

    @Test
    void deposit_fails_onNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> client.deposit(accCurrent, new BigDecimal("0.00"), "zero"));
        assertThrows(IllegalArgumentException.class,
                () -> client.deposit(accCurrent, new BigDecimal("-5.00"), "neg"));
    }

    @Test
    void withdraw_fails_onNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> client.withdraw(accCurrent, new BigDecimal("0.00"), "zero"));
        assertThrows(IllegalArgumentException.class,
                () -> client.withdraw(accCurrent, new BigDecimal("-1.00"), "neg"));
    }
}

