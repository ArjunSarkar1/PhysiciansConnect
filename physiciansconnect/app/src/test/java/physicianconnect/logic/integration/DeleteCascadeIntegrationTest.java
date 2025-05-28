package physicianconnect.logic.integration;

import org.junit.jupiter.api.*;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.*;
import physicianconnect.persistence.*;
import physicianconnect.persistence.interfaces.MedicationPersistence;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;
    import physicianconnect.persistence.interfaces.ReferralPersistence;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteCascadeIntegrationTest {

    private PhysicianManager physicianManager;
    private AppointmentManager appointmentManager;
    private MedicationPersistence medicationPersistence;
    private PrescriptionPersistence prescriptionPersistence;
    private ReferralPersistence referralPersistence;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, true);
        physicianManager = new PhysicianManager(PersistenceFactory.getPhysicianPersistence());
        appointmentManager = new AppointmentManager(PersistenceFactory.getAppointmentPersistence());
        medicationPersistence = PersistenceFactory.getMedicationPersistence();
        prescriptionPersistence = PersistenceFactory.getPrescriptionPersistence();
        referralPersistence = PersistenceFactory.getReferralPersistence();
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testDeletePhysicianRemovesAppointmentsPrescriptionsAndReferrals() {
        Physician doc = new Physician("p2", "Dr. Strange", "strange@hospital.com", "magic");
        physicianManager.addPhysician(doc);
        medicationPersistence.addMedication(new Medication("Morphine", "5mg", "Once", "Take with water"));
        appointmentManager.addAppointment(new Appointment("p2", "Wong", LocalDateTime.of(2025, 8, 1, 10, 0)));
        prescriptionPersistence.addPrescription(new Prescription(0, "p2", "Wong", "Morphine", "5mg", "5mg", "Once", "", "2025-08-01T10:00"));
        referralPersistence.addReferral(new Referral(0, "p2", "Wong", "Specialist", "See neurologist", "2025-08-01"));

        // Delete physician
        physicianManager.removePhysician("p2");

        System.out.println("Appointments: " + appointmentManager.getAppointmentsForPhysician("p2"));
System.out.println("Prescriptions: " + prescriptionPersistence.getPrescriptionsForPatient("Wong"));
System.out.println("Referrals by physician: " + referralPersistence.getReferralsForPhysician("p2"));
System.out.println("Referrals for patient: " + referralPersistence.getReferralsForPatient("Wong"));

        // Appointments, prescriptions, and referrals for p2 should be gone
        assertTrue(appointmentManager.getAppointmentsForPhysician("p2").isEmpty());
        assertTrue(prescriptionPersistence.getPrescriptionsForPatient("Wong").isEmpty());
        assertTrue(referralPersistence.getReferralsForPhysician("p2").isEmpty());
        assertTrue(referralPersistence.getReferralsForPatient("Wong").isEmpty());

    }
}