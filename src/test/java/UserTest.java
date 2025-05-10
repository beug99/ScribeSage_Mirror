import com.example.addressbook.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    private static final String FIRST_NAME = "John";
    private static final String FIRST_NAME_TWO = "Jane";
    private static final String LAST_NAME = "Doe";
    private static final String LAST_NAME_TWO = "Doe";
    private static final String EMAIL = "john@gmail.com";
    private static final String EMAIL_TWO = "jane@gmail.com";
    private static final String PASSWORD = "ooga";
    private static final String PASSSWORD_TWO = "booga";

    private User contact;
    private User contactTwo;

    @BeforeEach
    public void setUp() {
        contact = new User(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);
        contactTwo = new User(FIRST_NAME_TWO, LAST_NAME_TWO, EMAIL_TWO, PASSSWORD_TWO);
    }

    @Test
    public void testSetId() {
        contact.setUserId(1);
        assertEquals(1, contact.getUserId());
    }

    @Test
    public void testGetFirstName() {
        assertEquals(FIRST_NAME, contact.getFirstName());
    }
    @Test
    public void testSetFirstName() {
        contact.setFirstName(FIRST_NAME_TWO);
        assertEquals(FIRST_NAME_TWO, contact.getFirstName());
    }
    @Test
    public void testGetLastName() {
        assertEquals(LAST_NAME, contact.getLastName());
    }
    @Test
    public void testSetLastName() {
        contact.setLastName(LAST_NAME_TWO);
        assertEquals(LAST_NAME_TWO, contact.getLastName());
    }
    @Test
    public void testGetEmail() {
        assertEquals(EMAIL, contact.getEmail());
    }
    @Test
    public void testSetEmail() {
        contact.setEmail(EMAIL_TWO);
        assertEquals(EMAIL_TWO, contact.getEmail());
    }

    @Test
    public void testGetFullName() {
        String[] firstContact = {FIRST_NAME, LAST_NAME};
        String[] secondContact = {FIRST_NAME_TWO, LAST_NAME_TWO};
        assertEquals(String.join(" ", firstContact),  contact.getFullName());
        assertEquals(String.join(" ", secondContact),  contactTwo.getFullName());
    }
}
