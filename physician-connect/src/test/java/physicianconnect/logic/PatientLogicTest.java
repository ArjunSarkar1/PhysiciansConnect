package logic;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import logic.stub.PatientLogic;
import objects.Patient;
import java.util.Map;

public class PatientLogicTest {
    private PatientLogic patientLogic;
    private Patient testPatient;

    @Before
    public void setUp() {
        patientLogic = new PatientLogic();

        // Create test patient
        testPatient = new Patient(
                0, // ID will be assigned by the stub
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "123456789",
                "PHIN123456",
                null // medical history will be added later
        );
    }

    @Test
    public void testGetPatientById_ValidId() {
        // Add a patient first
        Patient added = patientLogic.addPatient(testPatient);
        assertNotNull("Added patient should not be null", added);

        // Test getting the patient
        Patient retrieved = patientLogic.getPatientById(added.getUserId());
        assertNotNull("Retrieved patient should not be null", retrieved);
        assertEquals("Patient IDs should match", added.getUserId(), retrieved.getUserId());
    }

    @Test
    public void testGetPatientById_InvalidId() {
        Patient retrieved = patientLogic.getPatientById(-1);
        assertNull("Retrieved patient should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllPatients() {
        // Add multiple patients
        Patient patient1 = patientLogic.addPatient(testPatient);
        Patient patient2 = patientLogic.addPatient(testPatient);

        Map<Integer, Patient> allPatients = patientLogic.getAllPatients();
        assertNotNull("All patients map should not be null", allPatients);
        assertTrue("Should contain at least 2 patients", allPatients.size() >= 2);
    }

    @Test
    public void testAddPatient_Valid() {
        Patient added = patientLogic.addPatient(testPatient);
        assertNotNull("Added patient should not be null", added);
        assertTrue("Patient ID should be positive", added.getUserId() > 0);
        assertEquals("Email should match", "john.doe@example.com", added.getEmail());
    }

    @Test
    public void testAddPatient_Null() {
        Patient added = patientLogic.addPatient(null);
        assertNull("Added patient should be null for null input", added);
    }

    @Test
    public void testUpdatePatient_Valid() {
        // Add a patient first
        Patient added = patientLogic.addPatient(testPatient);
        assertNotNull("Added patient should not be null", added);

        // Update the patient
        added.setEmail("updated.email@example.com");
        Patient updated = patientLogic.updatePatient(added);
        assertNotNull("Updated patient should not be null", updated);
        assertEquals("Email should be updated", "updated.email@example.com", updated.getEmail());
    }

    @Test
    public void testUpdatePatient_Invalid() {
        Patient updated = patientLogic.updatePatient(null);
        assertNull("Updated patient should be null for null input", updated);
    }

    @Test
    public void testDeletePatient_Valid() {
        // Add a patient first
        Patient added = patientLogic.addPatient(testPatient);
        assertNotNull("Added patient should not be null", added);

        // Delete the patient
        Patient deleted = patientLogic.deletePatient(added.getUserId());
        assertNotNull("Deleted patient should not be null", deleted);
        assertEquals("Deleted patient ID should match", added.getUserId(), deleted.getUserId());

        // Verify it's deleted
        Patient retrieved = patientLogic.getPatientById(added.getUserId());
        assertNull("Retrieved patient should be null after deletion", retrieved);
    }

    @Test
    public void testDeletePatient_Invalid() {
        Patient deleted = patientLogic.deletePatient(-1);
        assertNull("Deleted patient should be null for invalid ID", deleted);
    }
}