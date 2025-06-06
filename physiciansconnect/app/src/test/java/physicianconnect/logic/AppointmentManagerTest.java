package physicianconnect.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Physician;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;

public class AppointmentManagerTest {

    private AppointmentManager manager;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        // Add the physician for foreign key constraint
        PersistenceFactory.getPhysicianPersistence().addPhysician(
            new Physician("docX", "Dr. X", "x@x.com", "pw")
        );
        manager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testAddAndRetrieveAppointments() {
        int originalSize = manager.getAppointmentsForPhysician("docX").size();

        Appointment a = new Appointment("docX", "Peter Parker", LocalDateTime.now().plusMinutes(5));
        manager.addAppointment(a);

        List<Appointment> results = manager.getAppointmentsForPhysician("docX");
        assertEquals(originalSize + 1, results.size());
    }

    @Test
    public void testNoAppointmentsForUnknownPhysician() {
        List<Appointment> empty = manager.getAppointmentsForPhysician("ghost");
        assertTrue(empty.isEmpty());
    }
}