package physicianconnect.persistence.sqlite;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Notification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDBTest {
    private Connection conn;
    private NotificationDB db;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new NotificationDB(conn);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    void testAddAndFetchNotification() {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        List<Notification> list = db.getNotificationsForUser("uid", "utype");
        assertEquals(1, list.size());
        assertEquals("msg", list.get(0).getMessage());
    }

    @Test
    void testClearNotificationsForUser() {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        db.clearNotificationsForUser("uid", "utype");
        List<Notification> list = db.getNotificationsForUser("uid", "utype");
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetNotificationsForUserEmpty() {
        List<Notification> list = db.getNotificationsForUser("nouser", "notype");
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // --- Catch/exception coverage ---

    @Test
    void testAddNotificationCatchesSQLException() throws Exception {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        conn.close();
        assertDoesNotThrow(() -> db.addNotification(n)); // e.printStackTrace() is called, not thrown
    }

    @Test
    void testGetNotificationsForUserCatchesSQLException() throws Exception {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        conn.close();
        List<Notification> list = db.getNotificationsForUser("uid", "utype");
        assertNotNull(list);
        assertTrue(list.isEmpty()); // returns empty list on exception
    }

    @Test
    void testClearNotificationsForUserCatchesSQLException() throws Exception {
        Notification n = new Notification("msg", "type", LocalDateTime.now(), "uid", "utype");
        db.addNotification(n);
        conn.close();
        assertDoesNotThrow(() -> db.clearNotificationsForUser("uid", "utype")); // e.printStackTrace() is called, not thrown
    }
}