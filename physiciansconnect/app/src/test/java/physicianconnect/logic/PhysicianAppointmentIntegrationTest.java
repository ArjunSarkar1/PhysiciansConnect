package physicianconnect.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;

public class PhysicianAppointmentIntegrationTest {

    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testPhysicianAppointmentsArePersistedAndRetrieved() {
        // Step 1: Add physician
        Physician doc = new Physician("abc", "Dr. Who", "tardis@space.com", "timetravel");
        physicianManager.addPhysician(doc);

        // Step 2: Add appointment for that physician
        Appointment a = new Appointment("abc", "Amy Pond", LocalDateTime.of(2025, 1, 10, 9, 0));
        appointmentManager.addAppointment(a);

        // Step 3: Check that it's linked
        List<Appointment> result = appointmentManager.getAppointmentsForPhysician("abc");
        assertEquals(1, result.size());
        assertEquals("Amy Pond", result.get(0).getPatientName());
    }

    @Test
    public void testAppointmentForUnknownPhysicianReturnsEmpty() {
        List<Appointment> result = appointmentManager.getAppointmentsForPhysician("nonexistent-id");
        assertTrue(result.isEmpty());
    }
}
