package physicianconnect.logic;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Receptionist;
import physicianconnect.logic.manager.ReceptionistManager;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;

import static org.junit.jupiter.api.Assertions.*;

public class ReceptionistManagerTest {

    private ReceptionistManager manager;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        manager = new ReceptionistManager(PersistenceFactory.getReceptionistPersistence());
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testAddAndRetrieveReceptionist() {
        Receptionist r = new Receptionist("r001", "Alice", "alice@clinic.com", "pass");
        manager.addReceptionist(r);

        Receptionist fetched = manager.getReceptionistById("r001");
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getName());
    }

    @Test
    public void testDuplicateReceptionistIsIgnored() {
        Receptionist r1 = new Receptionist("r002", "First", "f@f.com", "pw");
        Receptionist r2 = new Receptionist("r002", "Second", "s@s.com", "pw");

        manager.addReceptionist(r1);
        manager.addReceptionist(r2);

        Receptionist result = manager.getReceptionistById("r002");
        assertEquals("First", result.getName());
    }

    @Test
    public void testLoginSuccess() {
        Receptionist r = new Receptionist("r123", "LoginTest", "login@r.com", "secret");
        manager.addReceptionist(r);

        Receptionist result = manager.login("login@r.com", "secret");
        assertNotNull(result);
        assertEquals("LoginTest", result.getName());
    }

    @Test
    public void testLoginFailureWrongPassword() {
        Receptionist r = new Receptionist("r200", "WrongPW", "wrong@pw.com", "realpw");
        manager.addReceptionist(r);

        Receptionist result = manager.login("wrong@pw.com", "wrongpw");
        assertNull(result);
    }

    @Test
    public void testLoginFailureUnknownEmail() {
        Receptionist result = manager.login("doesnotexist@domain.com", "whatever");
        assertNull(result);
    }

    @Test
    public void testValidationRejectsEmptyName() {
        Receptionist r = new Receptionist("r888", "", "valid@email.com", "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    public void testValidationRejectsInvalidEmail() {
        Receptionist r = new Receptionist("r999", "Name", "notAnEmail", "x");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> manager.validateBasicInfo(r));
        assertEquals("Invalid email format.", ex.getMessage());
    }

    @Test
    public void testValidationPassesValidInput() {
        Receptionist r = new Receptionist("r777", "Bob", "bob@clinic.com", "x");
        assertDoesNotThrow(() -> manager.validateBasicInfo(r));
    }

    @Test
    public void testValidateAndUpdateReceptionist() {
        Receptionist r = new Receptionist("rTest", "Original", "test@r.com", "pw");
        manager.addReceptionist(r);

        manager.validateAndUpdateReceptionist(r, "Updated Name", true, true, false);

        Receptionist updated = manager.getReceptionistById("rTest");
        assertEquals("Updated Name", updated.getName());
    }
}
