package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ConnectionManagerTest {

    @Test
    public void testConnectionManager_canInitializeAndClose() throws Exception {
        String dbUrl = ":memory:";

        ConnectionManager.initialize(dbUrl);
        assertNotNull(ConnectionManager.get(), "Connection should not be null after initialization");

        ConnectionManager.close();

        assertThrows(IllegalStateException.class, ConnectionManager::get,
                "Calling get() after close() should throw IllegalStateException");
    }
}

