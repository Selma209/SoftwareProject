package ch.unil.doplab.bankease.domain;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean loggedIn = false;

    public User(String username, String password, String firstName, String lastName,
                String email, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.email = Objects.requireNonNull(email);
        this.phoneNumber = Objects.requireNonNull(phoneNumber);
    }

    public User(User copy) {
        this.id = copy.id;
        this.username = copy.username;
        this.password = copy.password;
        this.firstName = copy.firstName;
        this.lastName = copy.lastName;
        this.email = copy.email;
        this.phoneNumber = copy.phoneNumber;
        this.loggedIn = copy.loggedIn;
    }

    public boolean login(String username, String password) {
        boolean ok = Objects.equals(this.username, username) && Objects.equals(this.password, password);
        this.loggedIn = ok;
        return ok;
    }
    public void logout() { this.loggedIn = false; }
    public boolean isLoggedIn() { return loggedIn; }

    public void editPersonalInfo(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.email = Objects.requireNonNull(email);
        this.phoneNumber = Objects.requireNonNull(phoneNumber);
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = Objects.requireNonNull(username); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = Objects.requireNonNull(password); }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return Objects.equals(id, ((User) o).id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
