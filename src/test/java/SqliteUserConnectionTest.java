import com.example.addressbook.model.SqliteUserConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteUserConnectionTest {

    private static final String TEST_URL = "jdbc:sqlite:test_users.db";

    @BeforeEach
    void setUp() {
        // reset connection before each test
        SqliteUserConnection.resetConnection(TEST_URL);
    }

    @AfterEach
    void tearDown() {
        // close connection after each test
        try {
            Connection connection = SqliteUserConnection.getInstance();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetInstance() {
        // first call should create connection
        Connection connection1 = SqliteUserConnection.getInstance();
        assertNotNull(connection1);

        Connection connection2 = SqliteUserConnection.getInstance();
        assertSame(connection1, connection2);
    }

    @Test
    void testResetConnection() {
        // get initial connection
        Connection connection1 = SqliteUserConnection.getInstance();

        // reset connection
        SqliteUserConnection.resetConnection(TEST_URL);

        // get new connection - should be different instance
        Connection connection2 = SqliteUserConnection.getInstance();
        assertNotSame(connection1, connection2);
    }

    @Test
    void testGetLastException() {
        SQLException exception = SqliteUserConnection.getLastException();
        assertNotNull(exception);
        assertEquals(SQLException.class, exception.getClass());
    }

}