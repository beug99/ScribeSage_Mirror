package com.example.addressbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    private static final String EMAIL = "john@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    private static final String NEW_EMAIL = "jane@example.com";
    private static final String NEW_FIRST_NAME = "Jane";
    private static final String NEW_LAST_NAME = "Smith";

    @BeforeEach
    public void clearSession() {
        Session.clear();
    }

    @Test
    public void testSetUserAndGetters() {
        Session.setUser(EMAIL, FIRST_NAME, LAST_NAME);

        assertEquals(EMAIL, Session.getLoggedInEmail());
        assertEquals(FIRST_NAME, Session.getFirstName());
        assertEquals(LAST_NAME, Session.getLastName());
    }

    @Test
    public void testSetLoggedInEmail() {
        Session.setLoggedInEmail(NEW_EMAIL);

        assertEquals(NEW_EMAIL, Session.getLoggedInEmail());
        assertNull(Session.getFirstName());
        assertNull(Session.getLastName());
    }

    @Test
    public void testClearSession() {
        Session.setUser(EMAIL, FIRST_NAME, LAST_NAME);
        Session.clear();

        assertNull(Session.getLoggedInEmail());
        assertNull(Session.getFirstName());
        assertNull(Session.getLastName());
    }
}
