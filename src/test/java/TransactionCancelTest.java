import ch.unil.doplab.bankease.domain.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionCancelTest {

    private Client c;
    private Account a1;
    private Account a2;

    @BeforeEach
    void init() {
        c = new Client("c","pw","C","User","c@x.y","0");
        a1 = c.openAccount(AccountType.CURRENT);
        a2 = c.openAccount(AccountType.SAVINGS);
        c.deposit(a1, new BigDecimal("500.00"), "seed");
    }

    @Test
    void cancel_deposit_reversesBalance() {
        // Dépôt déjà COMPLETED
        Transaction tx = Transaction.createDeposit(a1, new BigDecimal("100.00"), "d");
        // La création DEPOSIT applique déjà le dépôt via Client.deposit; ici on simule l'annulation business:
        tx.cancel();
        assertEquals(new BigDecimal("400.00"), a1.getBalance()); // 500 seed - 100 annulé
        assertEquals(TransactionStatus.CANCELED, tx.getStatus());
    }

    @Test
    void cancel_withdrawal_reversesBalance() {
        c.withdraw(a1, new BigDecimal("200.00"), "w");
        // Recréer un objet Withdrawal avec COMPLETED (pour tester cancel isolément)
        Transaction tx = Transaction.createWithdrawal(a1, new BigDecimal("50.00"), "w2");
        tx.cancel();
        // Annule le retrait -> remet 50
        assertEquals(new BigDecimal("350.00"), a1.getBalance()); // 500 -200 +50
        assertEquals(TransactionStatus.CANCELED, tx.getStatus());
    }

    @Test
    void cancel_transfer_onlyIfCompleted() {
        // Transfert qui se complète immédiatement (petit montant)
        Transaction tx = c.transfer(a1, a2, new BigDecimal("100.00"), "move");
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());

        // Annulation -> remet les soldes comme avant
        c.requestCancellation(tx);
        assertEquals(new BigDecimal("500.00"), a1.getBalance());
        assertEquals(new BigDecimal("0.00"), a2.getBalance());
        assertEquals(TransactionStatus.CANCELED, tx.getStatus());
    }

    @Test
    void cancel_transfer_pending_throws() {
        // Gros montant => pending
        Transaction tx = c.transfer(a1, a2, Employee.APPROVAL_THRESHOLD.add(new BigDecimal("1.00")), "big");
        assertEquals(TransactionStatus.PENDING_APPROVAL, tx.getStatus());
        assertThrows(IllegalStateException.class, tx::cancel);
    }
}

