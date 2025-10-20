package ch.unil.doplab;

import java.util.ArrayList;
import java.util.List;

public class Employee extends User {

    public enum Role {
    }

    // --- Attributes ---
    private Role role;
    private List<Client> managedClients;

    // --- Constructor ---
    public Employee(
            String userId, String username, String password,
            String firstName, String lastName, String email, String phoneNumber, Role role) {

        super(userId, username, password, firstName, lastName, email, phoneNumber);
        this.role = role;
        this.managedClients = new ArrayList<>();
    }

    public Role getRole() {
        return role;
    }

    public List<Client> getManagedClients() {
        return managedClients;
    }

}
