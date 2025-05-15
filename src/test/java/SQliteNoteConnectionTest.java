import com.example.addressbook.model.SqliteNoteConnection;
import com.example.addressbook.model.SqliteUserConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQliteNoteConnectionTest {

    private static final String TEST_DB_NAME = "test_notes.db";
    private static final String TEST_URL = "jdbc:sqlite";

    @BeforeEach
    void setUp() {
        // resetting connection before each test
        SqliteNoteConnection.resetConnection(TEST_URL);
    }

    @AfterEach
    void tearDown() {
        // closing connection after each test
        try {
            Connection connection = SqliteNoteConnection.getInstance(TEST_DB_NAME);
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetConnectionInstance() {
        // create connection
        Connection connection = SqliteNoteConnection.getInstance(TEST_DB_NAME);
        assertNotNull(connection);

        // second call to return the instance
        Connection connection1 = SqliteNoteConnection.getInstance(TEST_DB_NAME);
        assertSame(connection,connection1);
    }

    @Test
    void testResetConnection() {
        // get initial connection
        Connection connection1 = SqliteNoteConnection.getInstance(TEST_DB_NAME);
        // reset connection
        SqliteNoteConnection.resetConnection(TEST_URL);
        // get new connection - should be different instance
        Connection connection2 = SqliteNoteConnection.getInstance(TEST_DB_NAME);
        assertNotSame(connection1, connection2);
    }

    @Test
    void testGetLastException() {
        SQLException exception = SqliteNoteConnection.getLastException();
        assertNotNull(exception);
        assertEquals(SQLException.class, exception.getClass());
    }
}
