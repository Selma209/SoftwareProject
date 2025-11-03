import  ch.unil.doplab.bankease.domain.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void login_logout_and_editInfo_work() {
        User u = new User("u","pw","U","Ser","u@x.y","123");
        assertFalse(u.isLoggedIn());

        assertTrue(u.login("u", "pw"));
        assertTrue(u.isLoggedIn());

        u.editPersonalInfo("New", "Name", "n@x.y", "999");
        assertEquals("New", u.getFirstName());
        assertEquals("Name", u.getLastName());
        assertEquals("n@x.y", u.getEmail());
        assertEquals("999", u.getPhoneNumber());

        u.logout();
        assertFalse(u.isLoggedIn());
    }

    @Test
    void wrongCredentials_loginFails() {
        User u = new User("u","pw","U","Ser","u@x.y","123");
        assertFalse(u.login("u", "bad"));
        assertFalse(u.login("bad", "pw"));
        assertFalse(u.isLoggedIn());
    }

    @Test
    void setters_requireNonNull() {
        User u = new User("u","pw","U","Ser","u@x.y","123");
        assertThrows(NullPointerException.class, () -> u.setUsername(null));
        assertThrows(NullPointerException.class, () -> u.setPassword(null));
    }
}

