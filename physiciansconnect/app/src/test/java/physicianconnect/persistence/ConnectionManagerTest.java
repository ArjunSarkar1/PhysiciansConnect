package physicianconnect.persistence;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionManagerTest {

    @AfterEach
    void tearDown() {
        ConnectionManager.reset();
    }

    @Test
    void testInitializeAndGetConnection() throws Exception {
        ConnectionManager.initialize(":memory:");
        Connection conn = ConnectionManager.get();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        conn.close();
    }

    @Test
    void testCloseConnection() throws Exception {
        ConnectionManager.initialize(":memory:");
        Connection conn = ConnectionManager.get();
        ConnectionManager.close();
        assertTrue(conn.isClosed());
    }

    @Test
    void testResetDelegatesToClose() throws Exception {
        ConnectionManager.initialize(":memory:");
        Connection conn = ConnectionManager.get();
        ConnectionManager.reset();
        assertTrue(conn.isClosed());
    }

    @Test
    void testIsInitialized() {
        assertFalse(ConnectionManager.isInitialized());
        ConnectionManager.initialize(":memory:");
        assertTrue(ConnectionManager.isInitialized());
        ConnectionManager.close();
        assertFalse(ConnectionManager.isInitialized());
    }

    @Test
    void testCloseThrowsRuntimeExceptionOnSQLException() throws Exception {
        var field = ConnectionManager.class.getDeclaredField("connection");
        field.setAccessible(true);
        Connection mockConn = mock(Connection.class);
        doThrow(new SQLException("fail")).when(mockConn).close();
        field.set(null, mockConn);

        RuntimeException ex = assertThrows(RuntimeException.class, ConnectionManager::close);
        assertTrue(ex.getMessage().contains("Failed to close DB connection"));
        field.set(null, null);
    }

    @Test
    void testInitializeThrowsRuntimeExceptionOnSQLException() throws Exception {
        // Ensure static connection is null so initialize() does not return early
        var field = ConnectionManager.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(null, null);

        // Static mocking for DriverManager.getConnection
        try (var driverMock = org.mockito.Mockito.mockStatic(java.sql.DriverManager.class)) {
            driverMock.when(() -> java.sql.DriverManager.getConnection(anyString()))
                    .thenThrow(new SQLException("fail"));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> ConnectionManager.initialize("badpath"));
            assertTrue(ex.getMessage().contains("Failed to initialize DB connection"));
        }
    }
}