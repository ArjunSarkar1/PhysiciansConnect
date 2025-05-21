package physicianconnect.logic;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import physicianconnect.logic.stub.MedicalHistoryLogic;
import physicianconnect.objects.MedicalHistory;
import physicianconnect.objects.PatientVisitSummary;
import java.time.LocalDateTime;
import java.util.Map;

public class MedicalHistoryLogicTest {
    private MedicalHistoryLogic medicalHistoryLogic;
    private MedicalHistory testHistory;
    private PatientVisitSummary testVisitSummary;
    private LocalDateTime visitDate;

    @Before
    public void setUp() {
        medicalHistoryLogic = new MedicalHistoryLogic();
        visitDate = LocalDateTime.now();

        // Create test medical history
        testHistory = new MedicalHistory(
                0, // ID will be assigned by the stub
                1, // patientId
                "Hypertension",
                "High blood pressure diagnosis",
                "Prescribed medication",
                visitDate,
                "Regular monitoring required");

        // Create test visit summary
        testVisitSummary = new PatientVisitSummary(
                0, // ID will be assigned by the stub
                1, // patientId
                1, // physicianId
                visitDate,
                "Regular checkup",
                "Patient is stable",
                "Continue current medication",
                "Follow up in 3 months");
    }

    @Test
    public void testGetMedicalHistoryById_ValidId() {
        // Add a history first
        MedicalHistory added = medicalHistoryLogic.addMedicalHistory(testHistory);
        assertNotNull("Added history should not be null", added);

        // Test getting the history
        MedicalHistory retrieved = medicalHistoryLogic.getMedicalHistoryById(added.getMedicalHistoryId());
        assertNotNull("Retrieved history should not be null", retrieved);
        assertEquals("History IDs should match", added.getMedicalHistoryId(), retrieved.getMedicalHistoryId());
    }

    @Test
    public void testGetMedicalHistoryById_InvalidId() {
        MedicalHistory retrieved = medicalHistoryLogic.getMedicalHistoryById(-1);
        assertNull("Retrieved history should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllMedicalHistory() {
        // Add multiple histories
        MedicalHistory history1 = medicalHistoryLogic.addMedicalHistory(testHistory);
        MedicalHistory history2 = medicalHistoryLogic.addMedicalHistory(testHistory);

        Map<Integer, MedicalHistory> allHistories = medicalHistoryLogic.getAllMedicalHistory();
        assertNotNull("All histories map should not be null", allHistories);
        assertTrue("Should contain at least 2 histories", allHistories.size() >= 2);
    }

    @Test
    public void testAddMedicalHistory_Valid() {
        MedicalHistory added = medicalHistoryLogic.addMedicalHistory(testHistory);
        assertNotNull("Added history should not be null", added);
        assertTrue("History ID should be positive", added.getMedicalHistoryId() > 0);
        assertEquals("Condition should match", "Hypertension", added.getCondition());
    }

    @Test
    public void testAddMedicalHistory_Null() {
        MedicalHistory added = medicalHistoryLogic.addMedicalHistory(null);
        assertNull("Added history should be null for null input", added);
    }

    @Test
    public void testUpdateMedicalHistory_Valid() {
        // Add a history first
        MedicalHistory added = medicalHistoryLogic.addMedicalHistory(testHistory);
        assertNotNull("Added history should not be null", added);

        // Update the history
        added.setCondition("Updated condition");
        MedicalHistory updated = medicalHistoryLogic.updateMedicalHistory(added);
        assertNotNull("Updated history should not be null", updated);
        assertEquals("Condition should be updated", "Updated condition", updated.getCondition());
    }

    @Test
    public void testUpdateMedicalHistory_Invalid() {
        MedicalHistory updated = medicalHistoryLogic.updateMedicalHistory(null);
        assertNull("Updated history should be null for null input", updated);
    }

    @Test
    public void testDeleteMedicalHistory_Valid() {
        // Add a history first
        MedicalHistory added = medicalHistoryLogic.addMedicalHistory(testHistory);
        assertNotNull("Added history should not be null", added);

        // Delete the history
        MedicalHistory deleted = medicalHistoryLogic.deleteMedicalHistory(added.getMedicalHistoryId());
        assertNotNull("Deleted history should not be null", deleted);
        assertEquals("Deleted history ID should match", added.getMedicalHistoryId(), deleted.getMedicalHistoryId());

        // Verify it's deleted
        MedicalHistory retrieved = medicalHistoryLogic.getMedicalHistoryById(added.getMedicalHistoryId());
        assertNull("Retrieved history should be null after deletion", retrieved);
    }

    @Test
    public void testDeleteMedicalHistory_Invalid() {
        MedicalHistory deleted = medicalHistoryLogic.deleteMedicalHistory(-1);
        assertNull("Deleted history should be null for invalid ID", deleted);
    }

    @Test
    public void testGetVisitSummaryById_ValidId() {
        // Add a visit summary first
        PatientVisitSummary added = medicalHistoryLogic.addVisitSummary(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);

        // Test getting the visit summary
        PatientVisitSummary retrieved = medicalHistoryLogic.getVisitSummaryById(added.getSummaryId());
        assertNotNull("Retrieved visit summary should not be null", retrieved);
        assertEquals("Visit summary IDs should match", added.getSummaryId(), retrieved.getSummaryId());
    }

    @Test
    public void testGetVisitSummaryById_InvalidId() {
        PatientVisitSummary retrieved = medicalHistoryLogic.getVisitSummaryById(-1);
        assertNull("Retrieved visit summary should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllVisitSummaries() {
        // Add multiple visit summaries
        PatientVisitSummary summary1 = medicalHistoryLogic.addVisitSummary(testVisitSummary);
        PatientVisitSummary summary2 = medicalHistoryLogic.addVisitSummary(testVisitSummary);

        Map<Integer, PatientVisitSummary> allSummaries = medicalHistoryLogic.getAllVisitSummaries();
        assertNotNull("All visit summaries map should not be null", allSummaries);
        assertTrue("Should contain at least 2 visit summaries", allSummaries.size() >= 2);
    }

    @Test
    public void testAddVisitSummary_Valid() {
        PatientVisitSummary added = medicalHistoryLogic.addVisitSummary(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);
        assertTrue("Visit summary ID should be positive", added.getSummaryId() > 0);
        assertEquals("Reason should match", "Regular checkup", added.getReasonForVisit());
    }

    @Test
    public void testAddVisitSummary_Null() {
        PatientVisitSummary added = medicalHistoryLogic.addVisitSummary(null);
        assertNull("Added visit summary should be null for null input", added);
    }

    @Test
    public void testUpdateVisitSummary_Valid() {
        // Add a visit summary first
        PatientVisitSummary added = medicalHistoryLogic.addVisitSummary(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);

        // Update the visit summary
        added.setReasonForVisit("Updated reason");
        PatientVisitSummary updated = medicalHistoryLogic.updateVisitSummary(added);
        assertNotNull("Updated visit summary should not be null", updated);
        assertEquals("Reason should be updated", "Updated reason", updated.getReasonForVisit());
    }

    @Test
    public void testUpdateVisitSummary_Invalid() {
        PatientVisitSummary updated = medicalHistoryLogic.updateVisitSummary(null);
        assertNull("Updated visit summary should be null for null input", updated);
    }

    @Test
    public void testDeleteVisitSummary_Valid() {
        // Add a visit summary first
        PatientVisitSummary added = medicalHistoryLogic.addVisitSummary(testVisitSummary);
        assertNotNull("Added visit summary should not be null", added);

        // Delete the visit summary
        PatientVisitSummary deleted = medicalHistoryLogic.deleteVisitSummary(added.getSummaryId());
        assertNotNull("Deleted visit summary should not be null", deleted);
        assertEquals("Deleted visit summary ID should match", added.getSummaryId(), deleted.getSummaryId());

        // Verify it's deleted
        PatientVisitSummary retrieved = medicalHistoryLogic.getVisitSummaryById(added.getSummaryId());
        assertNull("Retrieved visit summary should be null after deletion", retrieved);
    }

    @Test
    public void testDeleteVisitSummary_Invalid() {
        PatientVisitSummary deleted = medicalHistoryLogic.deleteVisitSummary(-1);
        assertNull("Deleted visit summary should be null for invalid ID", deleted);
    }
}