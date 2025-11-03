import ch.unil.doplab.bankease.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferApprovalFlowTest {

    private Client alice;
    private Client bob;
    private Account a1;
    private Account b1;
    private Employee advisor; // rôle ADVISOR ou ADMINISTRATOR

    @BeforeEach
    void setUp() {
        alice = new Client("alice","pw","Alice","Smith","a@x.y","000");
        bob   = new Client("bob","pw","Bob","Jones","b@x.y","111");
        a1 = alice.openAccount(AccountType.CURRENT);
        b1 = bob.openAccount(AccountType.CURRENT);

        // Doter Alice
        alice.deposit(a1, new BigDecimal("10000.00"), "seed");

        advisor = new Employee("emp","pw","E","M","emp@bank.ch","999", Role.ADVISOR);
    }

    @Test
    void transfer_belowThreshold_completesImmediately() {
        BigDecimal amt = Employee.APPROVAL_THRESHOLD.subtract(new BigDecimal("1.00")); // just below threshold
        Transaction tx = alice.transfer(a1, b1, amt, "rent");
        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertEquals(a1, tx.getSource());
        assertEquals(b1, tx.getDestination());

        assertEquals(new BigDecimal("10000.00").subtract(amt), a1.getBalance());
        assertEquals(amt, b1.getBalance());
    }

    @Test
    void transfer_aboveThreshold_requiresApproval_thenCompletesOnApprove() {
        BigDecimal amt = Employee.APPROVAL_THRESHOLD.add(new BigDecimal("0.01")); // just above
        Transaction tx = alice.transfer(a1, b1, amt, "car");

        // À la création : en attente d'approbation, pas d'effet de solde
        assertEquals(TransactionStatus.PENDING_APPROVAL, tx.getStatus());
        assertEquals(new BigDecimal("10000.00"), a1.getBalance());
        assertEquals(BigDecimal.ZERO, b1.getBalance());

        // Approbation par employé autorisé
        advisor.approve(tx);

        // Après approbation : effets appliqués, statut COMPLETED
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertEquals(new BigDecimal("10000.00").subtract(amt), a1.getBalance());
        assertEquals(amt, b1.getBalance());
    }

    @Test
    void transfer_aboveThreshold_reject_keepsBalancesUnchanged() {
        BigDecimal amt = Employee.APPROVAL_THRESHOLD.add(new BigDecimal("100.00"));
        Transaction tx = alice.transfer(a1, b1, amt, "luxury");

        advisor.reject(tx);
        assertEquals(TransactionStatus.REJECTED, tx.getStatus());
        // Aucun mouvement de fonds
        assertEquals(new BigDecimal("10000.00"), a1.getBalance());
        assertEquals(BigDecimal.ZERO, b1.getBalance());
    }

    @Test
    void approve_or_reject_withoutPending_throws() {
        // transfert direct (complet)
        Transaction tx = alice.transfer(a1, b1, new BigDecimal("100.00"), "small");

        assertThrows(IllegalStateException.class, () -> advisor.approve(tx));
        assertThrows(IllegalStateException.class, () -> advisor.reject(tx));
    }
}

