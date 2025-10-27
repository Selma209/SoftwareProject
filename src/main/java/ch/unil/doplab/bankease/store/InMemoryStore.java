package ch.unil.doplab.bankease.store;

import ch.unil.doplab.bankease.domain.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStore {
    private final Map<String, Client> clients = new ConcurrentHashMap<>();
    private final Map<String, Employee> employees = new ConcurrentHashMap<>();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    // index pratiques
    private final Map<String, String> clientByUsername = new ConcurrentHashMap<>();
    private final Map<String, String> employeeByUsername = new ConcurrentHashMap<>();

    public Map<String, Client> clients() { return clients; }
    public Map<String, Employee> employees() { return employees; }
    public Map<String, Account> accounts() { return accounts; }
    public Map<String, Transaction> transactions() { return transactions; }

    public Map<String, String> clientByUsername() { return clientByUsername; }
    public Map<String, String> employeeByUsername() { return employeeByUsername; }
}
