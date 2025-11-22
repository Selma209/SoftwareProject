package ch.unil.doplab.bankease.config;

import ch.unil.doplab.bankease.domain.*;
import ch.unil.doplab.bankease.store.InMemoryStore;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@ApplicationScoped
public class DataLoader {

    @Inject
    private InMemoryStore store;

    @PostConstruct
    public void init() {

        // === Exemple d’un utilisateur CLIENT ===
        Client client = new Client(
                "selmaUser",          // username
                "password123",        // password
                "Selma",              // firstName
                "Client",             // lastName
                "selma@mail.com",     // email
                "0781234567"          // phone
        );

        // On place le client dans le store
        store.clients().put(client.getId(), client);
        store.clientByUsername().put(client.getUsername(), client.getId());

        // == Création de comptes ==
        Account current = client.openAccount(AccountType.CURRENT);
        client.deposit(current, new BigDecimal("1500.00"), "Initial deposit");
        store.accounts().put(current.getAccountNumber(), current);

        Account savings = client.openAccount(AccountType.SAVINGS);
        client.deposit(savings, new BigDecimal("3500.00"), "Initial savings");
        store.accounts().put(savings.getAccountNumber(), savings);

        // == Exemple d’un employé ==
        Employee emp = new Employee(
                "employee1",
                "pass123",
                "John",
                "Doe",
                "john@bank.com",
                "0789988776",
                Role.EMPLOYEE
        );

        store.employees().put(emp.getId(), emp);
        store.employeeByUsername().put(emp.getUsername(), emp.getId());
    }
}
