package physicianconnect.logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;
import java.nio.file.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.*;

public class PhysicianManagerTest {

    private PhysicianManager manager;

    @BeforeEach
    public void setup() {
        try {
            Files.deleteIfExists(Paths.get("test.db"));
        } catch (Exception e) {
            // Ignore
        }
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        manager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testAddAndRetrievePhysician() {
        Physician p = new Physician("id123", "Dr. Alice", "alice@email.com", "password123");
        manager.addPhysician(p);

        Physician fetched = manager.getPhysicianById("id123");
        assertNotNull(fetched);
        assertEquals("Dr. Alice", fetched.getName());
    }

    @Test
    public void testDuplicatePhysicianIsIgnored() {
        Physician first = new Physician("same", "Dr. A", "a@a.com", "pw1");
        Physician duplicate = new Physician("same", "Dr. B", "b@b.com", "pw2");

        manager.addPhysician(first);
        manager.addPhysician(duplicate);

        Physician result = manager.getPhysicianById("same");
        assertEquals("Dr. A", result.getName());
        assertEquals("pw1", result.getPassword());
    }

    @Test
    public void testLoginSuccess() {
        Physician p = new Physician("login", "Dr. X", "x@x.com", "12345");
        manager.addPhysician(p);

        Physician result = manager.login("x@x.com", "12345");
        assertNotNull(result);
        assertEquals("Dr. X", result.getName());
    }

    @Test
    public void testLoginFailureWrongPassword() {
        Physician p = new Physician("badpw", "Dr. Y", "y@y.com", "realpw");
        manager.addPhysician(p);

        Physician result = manager.login("y@y.com", "wrongpw");
        assertNull(result);
    }

    @Test
    public void testLoginFailureUnknownEmail() {
        Physician result = manager.login("notfound@email.com", "pw");
        assertNull(result);
    }

    @Test
    public void testValidationRejectsInvalidPhone() {
        Physician p = new Physician("3", "Dr. BadPhone", "bad@clinic.com", "x");
        p.setPhone("123456");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(p));
        assertEquals("Phone must match (204) 123-4567 format.", ex.getMessage());
    }

    @Test
    public void testValidationPassesValidPhone() {
        Physician p = new Physician("4", "Dr. Good", "good@clinic.com", "x");
        p.setPhone("(204) 123-4567");
        assertDoesNotThrow(() -> manager.validateBasicInfo(p));
    }

    @Test
    public void testUpdatePhysicianNotificationSettings() {
        Physician p = new Physician("2", "Dr. Notify", "notify@clinic.com", "123");
        p.setNotifyAppointment(false);
        p.setNotifyBilling(true);
        p.setNotifyMessages(false);
        manager.addPhysician(p);

        p.setNotifyAppointment(true);
        p.setNotifyMessages(true);
        manager.updatePhysician(p);

        Physician updated = manager.getPhysicianById("2");
        assertTrue(updated.isNotifyAppointment());
        assertTrue(updated.isNotifyMessages());
        assertTrue(updated.isNotifyBilling());
    }
}
