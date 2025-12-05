package ch.unil.doplab.bankease.store;

import ch.unil.doplab.bankease.domain.Account;
import ch.unil.doplab.bankease.domain.AccountType;
import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.domain.Employee;
import ch.unil.doplab.bankease.domain.Transaction;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryStore rétro-compatible :
 * - conserve les maps historiques (clients/employees/accounts/transactions) avec getters d'instance
 * - ajoute un sous-store de soldes par compte (balances)
 * - expose des MÉTHODES STATIQUES (exists/get/deposit/withdraw/create/remove) attendues par tes resources
 */
@ApplicationScoped
public class InMemoryStore {

    // ====== HISTORIQUE (compat services) : maps d'objets du domaine ======
    private final Map<String, Client> clients = new ConcurrentHashMap<>();
    private final Map<String, Employee> employees = new ConcurrentHashMap<>();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    // index pratiques
    private final Map<String, String> clientByUsername = new ConcurrentHashMap<>();
    private final Map<String, String> employeeByUsername = new ConcurrentHashMap<>();

    // Getters d'INSTANCE (utilisés par *ServiceImpl via 'store.accounts()' etc.)
    public Map<String, Client> clients() { return clients; }
    public Map<String, Employee> employees() { return employees; }
    public Map<String, Account> accounts() { return accounts; }
    public Map<String, Transaction> transactions() { return transactions; }
    public Map<String, String> clientByUsername() { return clientByUsername; }
    public Map<String, String> employeeByUsername() { return employeeByUsername; }

    // ====== NOUVEAU : soldes par compte (en CHF) ======
    private final ConcurrentHashMap<String, Double> balances = new ConcurrentHashMap<>();

    // ====== SINGLETON interne pour les méthodes statiques ======
    private static final InMemoryStore INSTANCE = new InMemoryStore();

    public InMemoryStore() {
        // Seed de soldes de démo (historique)
        balances.putIfAbsent("ACC-001", 1000.0);
        balances.putIfAbsent("ACC-002", 500.0);
        balances.putIfAbsent("ACC-003", 0.0);


        Client john = new Client(
                "john",       // username
                "xyz",               // password
                "John",              // firstName
                "Doe",               // lastName
                "john@example.com",  // email
                "123456789"         // phone

        );
        addClient(john);

        Account johnAcc = john.openAccount(AccountType.CURRENT);
        accounts.put(johnAcc.getAccountNumber(), johnAcc);
        balances.put(johnAcc.getAccountNumber(), 500.0);

        Client mary = new Client(
                "mary",
                "abcd",
                "Mary",
                "Smith",
                "mary@example.com",
                "987654321"
        );
        addClient(mary);

        Account maryAcc = mary.openAccount(AccountType.SAVINGS);
        accounts.put(maryAcc.getAccountNumber(), maryAcc);
        balances.put(maryAcc.getAccountNumber(), 1200.0); // solde initial
    }

    // ----- Méthodes d'instance pour les soldes -----
    public void ensureAccountBalance(String accountNumber) {
        balances.putIfAbsent(accountNumber, 0.0);
    }

    public void removeAccountBalance(String accountNumber) {
        balances.remove(accountNumber);
    }

    public double getBalance(String accountNumber) {
        return balances.getOrDefault(accountNumber, 0.0);
    }

    public void addClient(Client client) {
        // ici tu stockais par username, je respecte ton choix
        clients.put(client.getUsername(), client); // stocké par username
        clientByUsername.put(client.getUsername(), client.getUsername());
    }

    public double depositBalance(String accountNumber, double amount) {
        ensureAccountBalance(accountNumber);
        return balances.merge(accountNumber, amount, Double::sum);
    }

    public boolean withdrawBalance(String accountNumber, double amount) {
        while (true) {
            Double current = balances.get(accountNumber);
            if (current == null) return false;
            if (current < amount) return false;
            double next = current - amount;
            if (balances.replace(accountNumber, current, next)) {
                return true;
            }
        }
    }

    public Map<String, Double> allBalances() {
        return Collections.unmodifiableMap(balances);
    }

    // ====== API STATIQUE rétro-compatible (utilisée par tes resources/services) ======

    /** Alias statique pour créer un compte côté soldes (solde initial 0). */
    public static void create(String accountNumber) {
        INSTANCE.ensureAccountBalance(accountNumber);
    }

    /** Alias statique pour supprimer un compte côté soldes. */
    public static void remove(String accountNumber) {
        INSTANCE.removeAccountBalance(accountNumber);
    }

    /** Alias statique : existe ? */
    public static boolean exists(String accountNumber) {
        return INSTANCE.balances.containsKey(accountNumber);
    }

    /** Alias statique : obtenir le solde (0 si inconnu). */
    public static double get(String accountNumber) {
        return INSTANCE.getBalance(accountNumber);
    }

    /** Alias statique : dépôt → retourne le nouveau solde. */
    public static double deposit(String accountNumber, double amount) {
        return INSTANCE.depositBalance(accountNumber, amount);
    }

    /** Alias statique : retrait sécurisé → true si effectué, false si insuffisant/inexistant. */
    public static boolean withdraw(String accountNumber, double amount) {
        return INSTANCE.withdrawBalance(accountNumber, amount);
    }

    /** Vue read-only de tous les soldes (utile pour GET all). */
    public static Map<String, Double> all() {
        return INSTANCE.allBalances();
    }

    /** Accès au singleton si besoin. */
    public static InMemoryStore getInstance() {
        return INSTANCE;
    }
}
