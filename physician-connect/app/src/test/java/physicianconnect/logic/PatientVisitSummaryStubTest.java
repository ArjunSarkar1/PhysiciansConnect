package physicianconnect.persistence;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import physicianconnect.objects.PatientVisitSummary;
import physicianconnect.persistence.stub.PatientVisitSummaryStub;

import java.time.LocalDateTime;
import java.util.Map;

public class PatientVisitSummaryStubTest {
    private PatientVisitSummaryStub visitSummaryStub;
    private PatientVisitSummary testVisitSummary;
    private LocalDateTime visitDateTime;

    @Before
    public void setUp() {
        visitSummaryStub = PatientVisitSummaryStub.getInstance();
        visitDateTime = LocalDateTime.now();
        
        // Create test visit summary
        testVisitSummary = new PatientVisitSummary(
            0, // ID will be assigned by the stub
            visitDateTime,
            "Dr. John Smith",
            1, // officeId
            30, // durationMins
            "Regular checkup",
            "Good experience",
            "Dr. Sarah Johnson", // referralName
            "Amoxicillin 500mg" // prescribedMedications
        );
    }

    @Test
    public void testGetPatientVisitSummary_ValidId() {
        // Add a visit summary first
        PatientVisitSummary added = visitSummaryStub.addPatientVisitSummaryy(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);
        
        // Test getting the visit summary
        PatientVisitSummary retrieved = visitSummaryStub.getPatientVisitSummary(added.getVisitId());
        assertNotNull("Retrieved visit summary should not be null", retrieved);
        assertEquals("Visit IDs should match", added.getVisitId(), retrieved.getVisitId());
        assertEquals("Physician name should match", "Dr. John Smith", retrieved.getPhysicianName());
        assertEquals("Duration should match", 30, retrieved.getDurationMins());
    }

    @Test
    public void testGetPatientVisitSummary_InvalidId() {
        PatientVisitSummary retrieved = visitSummaryStub.getPatientVisitSummary(-1);
        assertNull("Retrieved visit summary should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllVisitSummaries() {
        // Add multiple visit summaries
        PatientVisitSummary summary1 = visitSummaryStub.addPatientVisitSummaryy(testVisitSummary);
        PatientVisitSummary summary2 = visitSummaryStub.addPatientVisitSummaryy(testVisitSummary);
        
        Map<Integer, PatientVisitSummary> allSummaries = visitSummaryStub.getAllVisitSummaries();
        assertNotNull("All visit summaries map should not be null", allSummaries);
        assertTrue("Should contain at least 2 visit summaries", allSummaries.size() >= 2);
    }

    @Test
    public void testAddPatientVisitSummary_Valid() {
        PatientVisitSummary added = visitSummaryStub.addPatientVisitSummaryy(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);
        assertTrue("Visit ID should be positive", added.getVisitId() > 0);
        assertEquals("Reason for visit should match", "Regular checkup", added.getReasonForVisit());
        assertEquals("Patient feedback should match", "Good experience", added.getPatientFeedback());
    }

    @Test
    public void testAddPatientVisitSummary_Null() {
        PatientVisitSummary added = visitSummaryStub.addPatientVisitSummaryy(null);
        assertNull("Added visit summary should be null for null input", added);
    }

    @Test
    public void testUpdatePatientVisitSummary_Valid() {
        // Add a visit summary first
        PatientVisitSummary added = visitSummaryStub.addPatientVisitSummaryy(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);
        
        // Update the visit summary
        added.setPatientFeedback("Excellent experience");
        PatientVisitSummary updated = visitSummaryStub.updatePatientVisitSummary(added);
        assertNotNull("Updated visit summary should not be null", updated);
        assertEquals("Patient feedback should be updated", "Excellent experience", updated.getPatientFeedback());
    }

    @Test
    public void testUpdatePatientVisitSummary_Invalid() {
        PatientVisitSummary updated = visitSummaryStub.updatePatientVisitSummary(null);
        assertNull("Updated visit summary should be null for null input", updated);
    }

    @Test
    public void testDeletePatientVisitSummary_Valid() {
        // Add a visit summary first
        PatientVisitSummary added = visitSummaryStub.addPatientVisitSummaryy(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);
        
        // Delete the visit summary
        PatientVisitSummary deleted = visitSummaryStub.deletePatientVisitSummary(added.getVisitId());
        assertNotNull("Deleted visit summary should not be null", deleted);
        assertEquals("Deleted visit ID should match", added.getVisitId(), deleted.getVisitId());
        
        // Verify it's deleted
        PatientVisitSummary retrieved = visitSummaryStub.getPatientVisitSummary(added.getVisitId());
        assertNull("Retrieved visit summary should be null after deletion", retrieved);
    }

    @Test
    public void testDeletePatientVisitSummary_Invalid() {
        PatientVisitSummary deleted = visitSummaryStub.deletePatientVisitSummary(-1);
        assertNull("Deleted visit summary should be null for invalid ID", deleted);
    }
} 