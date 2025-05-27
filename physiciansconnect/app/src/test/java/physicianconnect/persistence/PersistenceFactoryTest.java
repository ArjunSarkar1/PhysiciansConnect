package physicianconnect.persistence;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceFactoryTest {

    @BeforeEach
    public void setup() {
        PersistenceFactory.reset();
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testInitializeProd() {
        PersistenceFactory.initialize(PersistenceType.PROD, false);
        assertNotNull(PersistenceFactory.getPhysicianPersistence());
        assertNotNull(PersistenceFactory.getAppointmentPersistence());
        assertNotNull(PersistenceFactory.getMedicationPersistence());
        assertNotNull(PersistenceFactory.getPrescriptionPersistence());
    }

    @Test
    public void testInitializeTest() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        assertNotNull(PersistenceFactory.getPhysicianPersistence());
        assertNotNull(PersistenceFactory.getAppointmentPersistence());
        assertNotNull(PersistenceFactory.getMedicationPersistence());
        assertNotNull(PersistenceFactory.getPrescriptionPersistence());
    }

    @Test
    public void testFallbackToStubs() {
        PersistenceFactory.initialize(PersistenceType.STUB, false);
        assertNotNull(PersistenceFactory.getPhysicianPersistence());
        assertNotNull(PersistenceFactory.getAppointmentPersistence());
        assertNotNull(PersistenceFactory.getMedicationPersistence());
        assertNotNull(PersistenceFactory.getPrescriptionPersistence());
    }
}