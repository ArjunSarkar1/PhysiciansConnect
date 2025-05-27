package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteCascadeIntegrationTest {

    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;
    private MedicationPersistence medicationPersistence;
    private PrescriptionPersistence prescriptionPersistence;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true);
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
    public void testDeletePhysicianRemovesAppointmentsAndPrescriptions() {
        Physician doc = new Physician("p2", "Dr. Strange", "strange@hospital.com", "magic");
        physicianManager.addPhysician(doc);
        medicationPersistence.addMedication(new Medication("Morphine", "5mg"));
        appointmentManager.addAppointment(new Appointment("p2", "Wong", LocalDateTime.of(2025, 8, 1, 10, 0)));
        prescriptionPersistence.addPrescription(new Prescription(0, "p2", "Wong", "Morphine", "5mg", "5mg", "Once", "", "2025-08-01T10:00"));

        // Delete physician
        physicianManager.removePhysician("p2");

        // Appointments and prescriptions for p2 should be gone
        assertTrue(appointmentManager.getAppointmentsForPhysician("p2").isEmpty());
        assertTrue(prescriptionPersistence.getPrescriptionsForPatient("Wong").isEmpty());
    }
}