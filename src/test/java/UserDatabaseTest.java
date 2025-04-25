import com.example.addressbook.model.SqliteNoteConnection;
import com.example.addressbook.model.SqliteUserConnection;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDatabaseTest {

    @Test
    void UserTestExceptionThrownIfInvalidURL() {
        SqliteUserConnection.resetConnection("jdbc:sqlite:/invalid/pathToDB");

        SQLException exception = SqliteUserConnection.getLastException();

        assertNotNull(exception, "SQLException caught");
        assertTrue(exception.getMessage().contains("path") || exception.getMessage().contains("unable"),
        "Exception message should indicate connection issue.");
    }

    @Test
    void NoteTestExceptionThrownIfInvalidURL() {
        SqliteNoteConnection.resetConnection("jdbc:sqlite:/invalid/pathToDB");

        SQLException exception = SqliteNoteConnection.getLastException();

        assertNotNull(exception, "SQLException caught");
        assertTrue(exception.getMessage().contains("path") || exception.getMessage().contains("unable"),
                "Exception message should indicate connection issue.");
    }
}
