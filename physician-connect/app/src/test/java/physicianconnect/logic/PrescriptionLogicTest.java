package physicianconnect.logic;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import physicianconnect.logic.stub.PrescriptionLogic;
import physicianconnect.objects.Prescription;
import java.time.LocalDateTime;
import java.util.Map;

public class PrescriptionLogicTest {
    private PrescriptionLogic prescriptionLogic;
    private Prescription testPrescription;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Before
    public void setUp() {
        prescriptionLogic = new PrescriptionLogic();
        
        // Create test dates
        startDate = LocalDateTime.now();
        endDate = startDate.plusMonths(1);
        
        // Create test prescription
        testPrescription = new Prescription(
            0, // ID will be assigned by the stub
            "Amoxicillin",
            "500mg",
            "Twice daily",
            "Take with food",
            startDate,
            endDate
        );
    }

    @Test
    public void testGetPrescriptionById_ValidId() {
        // Add a prescription first
        Prescription added = prescriptionLogic.addPrescription(testPrescription);
        assertNotNull("Added prescription should not be null", added);
        
        // Test getting the prescription
        Prescription retrieved = prescriptionLogic.getPrescriptionById(added.getPrescriptionId());
        assertNotNull("Retrieved prescription should not be null", retrieved);
        assertEquals("Prescription IDs should match", added.getPrescriptionId(), retrieved.getPrescriptionId());
    }

    @Test
    public void testGetPrescriptionById_InvalidId() {
        Prescription retrieved = prescriptionLogic.getPrescriptionById(-1);
        assertNull("Retrieved prescription should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllPrescriptions() {
        // Add multiple prescriptions
        Prescription prescription1 = prescriptionLogic.addPrescription(testPrescription);
        Prescription prescription2 = prescriptionLogic.addPrescription(testPrescription);
        
        Map<Integer, Prescription> allPrescriptions = prescriptionLogic.getAllPrescriptions();
        assertNotNull("All prescriptions map should not be null", allPrescriptions);
        assertTrue("Should contain at least 2 prescriptions", allPrescriptions.size() >= 2);
    }

    @Test
    public void testAddPrescription_Valid() {
        Prescription added = prescriptionLogic.addPrescription(testPrescription);
        assertNotNull("Added prescription should not be null", added);
        assertTrue("Prescription ID should be positive", added.getPrescriptionId() > 0);
        assertEquals("Name should match", "Amoxicillin", added.getName());
        assertEquals("Dosage should match", "500mg", added.getDosage());
    }

    @Test
    public void testAddPrescription_Null() {
        Prescription added = prescriptionLogic.addPrescription(null);
        assertNull("Added prescription should be null for null input", added);
    }

    @Test
    public void testUpdatePrescription_Valid() {
        // Add a prescription first
        Prescription added = prescriptionLogic.addPrescription(testPrescription);
        assertNotNull("Added prescription should not be null", added);
        
        // Update the prescription
        added.setDosage("750mg");
        Prescription updated = prescriptionLogic.updatePrescription(added);
        assertNotNull("Updated prescription should not be null", updated);
        assertEquals("Dosage should be updated", "750mg", updated.getDosage());
    }

    @Test
    public void testUpdatePrescription_Invalid() {
        Prescription updated = prescriptionLogic.updatePrescription(null);
        assertNull("Updated prescription should be null for null input", updated);
    }

    @Test
    public void testDeletePrescription_Valid() {
        // Add a prescription first
        Prescription added = prescriptionLogic.addPrescription(testPrescription);
        assertNotNull("Added prescription should not be null", added);
        
        // Delete the prescription
        Prescription deleted = prescriptionLogic.deletePrescription(added.getPrescriptionId());
        assertNotNull("Deleted prescription should not be null", deleted);
        assertEquals("Deleted prescription ID should match", added.getPrescriptionId(), deleted.getPrescriptionId());
        
        // Verify it's deleted
        Prescription retrieved = prescriptionLogic.getPrescriptionById(added.getPrescriptionId());
        assertNull("Retrieved prescription should be null after deletion", retrieved);
    }

    @Test
    public void testDeletePrescription_Invalid() {
        Prescription deleted = prescriptionLogic.deletePrescription(-1);
        assertNull("Deleted prescription should be null for invalid ID", deleted);
    }
} 