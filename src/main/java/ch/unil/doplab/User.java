package ch.unil.doplab;
public abstract class User {

    // --- Attributes ---
    private String userId;      // [cite: 15]
    private String username;    // [cite: 16]
    private String password;    // [cite: 16]
    private String firstName;   // [cite: 17]
    private String lastName;    // [cite: 17]
    private String email;       // [cite: 18]
    private String phoneNumber; // [cite: 19]

    // --- Constructor ---
    public User(String userId, String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.password = password; // In a real app, this would be hashed
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public boolean logIn(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    public void logOut() {
        System.out.println(username + " logged out.");
    }

    public void editPersonalInfo(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}