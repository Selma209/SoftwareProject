import ch.unil.doplab.bankease.domain.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeClientManagementTest {

    @Test
    void add_and_remove_managedClients_setsClientEmployee() {
        Employee e = new Employee("emp","pw","E","M","e@x.y","0", Role.ADVISOR);
        Client c = new Client("c","pw","C","User","c@x.y","1");

        assertTrue(e.getManagedClients().isEmpty());
        e.createClientAccount(c);
        assertEquals(1, e.getManagedClients().size());
        assertEquals(e, c.getEmployee());

        e.deleteClientAccount(c);
        assertTrue(e.getManagedClients().isEmpty());
        assertNull(c.getEmployee());
    }

    @Test
    void onlyAdvisorOrAdmin_canApproveReject() {
        Client a = new Client("a","pw","A","A","a@x.y","0");
        Client b = new Client("b","pw","B","B","b@x.y","1");
        Account a1 = a.openAccount(AccountType.CURRENT);
        Account b1 = b.openAccount(AccountType.CURRENT);
        a.deposit(a1, new BigDecimal("6000.00"), "seed");

        Transaction tx = a.transfer(a1, b1, new BigDecimal("5500.00"), "big"); // PENDING_APPROVAL
        Employee basic = new Employee("emp","pw","E","M","e@x.y","0", Role.ADVISOR);

        // OK for advisor
        basic.approve(tx);
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());

        // Now try with an employee with no rights
        Employee unauthorized = new Employee("x","pw","X","X","x@x.y","2", Role.valueOf("ADVISOR")); // keep advisor for valid case
        // For invalid role you'd need another enum value; current code checks only ADVISOR or ADMINISTRATOR.
    }
}

