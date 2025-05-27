package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FullWorkflowIntegrationTest {

    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;
    private MedicationPersistence medicationPersistence;
    private PrescriptionPersistence prescriptionPersistence;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true); // Seed DB
        physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
        medicationPersistence = PersistenceFactory.getMedicationPersistence();
        prescriptionPersistence = PersistenceFactory.getPrescriptionPersistence();
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testPhysicianCanScheduleAndPrescribe() {
        // Add physician
        Physician doc = new Physician("p1", "Dr. House", "house@hospital.com", "vicodin");
        physicianManager.addPhysician(doc);

        // Add medication
        Medication med = new Medication("Vicodin", "10mg", "Pain relief", "Take with water");
        medicationPersistence.addMedication(med);

        // Schedule appointment
        Appointment appt = new Appointment("p1", "Gregory", LocalDateTime.of(2025, 7, 1, 14, 0));
        appointmentManager.addAppointment(appt);

        // Prescribe medication
        Prescription pres = new Prescription(0, "p1", "Gregory", "Vicodin", "10mg", "10mg", "Once", "Take with water", "2025-07-01T14:00");
        prescriptionPersistence.addPrescription(pres);

        // Assert all data is present
        assertNotNull(physicianManager.getPhysicianById("p1"));
        assertFalse(appointmentManager.getAppointmentsForPhysician("p1").isEmpty());
        assertFalse(medicationPersistence.getAllMedications().isEmpty());
        assertFalse(prescriptionPersistence.getPrescriptionsForPatient("Gregory").isEmpty());
    }
}