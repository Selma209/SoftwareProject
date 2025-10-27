package ch.unil.doplab.bankease.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee extends User {
    public static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("5000.00");
    private Role role;
    private final List<Client> managedClients = new ArrayList<>();

    public Employee(String username, String password, String firstName, String lastName,
                    String email, String phoneNumber, Role role) {
        super(username, password, firstName, lastName, email, phoneNumber);
        this.role = Objects.requireNonNull(role);
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = Objects.requireNonNull(role); }

    public List<Client> getManagedClients() { return List.copyOf(managedClients); }

    public void createClientAccount(Client client) {
        if (client == null) return;
        if (!managedClients.contains(client)) {
            managedClients.add(client);
            client.setEmployee(this);
        }
    }

    public void deleteClientAccount(Client client) {
        if (client == null) return;
        if (managedClients.remove(client)) {
            client.setEmployee(null);
        }
    }

    public void approve(Transaction tx) {
        requireAdminOrAdvisor();
        if (tx.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Transaction is not pending approval.");
        }
        tx.markApproved();
        tx.applyEffects();
    }

    public void reject(Transaction tx) {
        requireAdminOrAdvisor();
        if (tx.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Transaction is not pending approval.");
        }
        tx.markRejected();
    }

    private void requireAdminOrAdvisor() {
        if (role != Role.ADMINISTRATOR && role != Role.ADVISOR) {
            throw new SecurityException("Insufficient rights.");
        }
    }
}
