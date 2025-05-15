import com.example.addressbook.helper.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.password4j.BadParametersException;
import com.password4j.Password;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class PasswordHasherTest {

    private static final String PLAIN_PASSWORD = "securePassword123";
    private static final String HASHED_PASSWORD = "$argon2id$v=19$m=65536,t=2,p=1$abcdefghijklmnop$qrstuvwxyz123456789ABCDEFGHI";

    @Test
    void testHashPassword() {
        String hashedPassword = PasswordHasher.hashPassword(PLAIN_PASSWORD);

        // The hash should not be null or empty
        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());

        // The hash format should match Argon2 format (starting with $argon2)
        assertTrue(hashedPassword.startsWith("$argon2"));

        // Each hash should be unique (even for the same password)
        String secondHash = PasswordHasher.hashPassword(PLAIN_PASSWORD);
        assertNotEquals(hashedPassword, secondHash);
    }

    @Test
    void testVerifyPasswordWithCorrectPassword() {
        // First hash a password
        String hashedPassword = PasswordHasher.hashPassword(PLAIN_PASSWORD);

        // Then verify it
        boolean result = PasswordHasher.verifyPassword(PLAIN_PASSWORD, hashedPassword);

        assertTrue(result);
    }

    @Test
    void testVerifyPasswordWithIncorrectPassword() {
        // First hash a password
        String hashedPassword = PasswordHasher.hashPassword(PLAIN_PASSWORD);

        // Then verify with wrong password
        boolean result = PasswordHasher.verifyPassword("wrongPassword", hashedPassword);

        assertFalse(result);
    }
}
