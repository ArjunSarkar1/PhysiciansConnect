package physicianconnect.logic;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import physicianconnect.logic.stub.MedicalHistoryLogic;
import physicianconnect.objects.MedicalHistory;
import java.util.Map;

public class MedicalHistoryLogicTest {
    private MedicalHistoryLogic medicalHistoryLogic;
    private MedicalHistory testHistory;

    @Before
    public void setUp() {
        medicalHistoryLogic = new MedicalHistoryLogic();

        // Create test medical history
        testHistory = new MedicalHistory(
                0, // ID will be assigned by the stub
                "Hypertension", // pastConditions
                "None", // surgeries
                "None", // allergies
                "Up to date", // immunizations
                "None", // hospitalizations
                "No significant family history" // familyHistory
        );
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
        assertEquals("Past conditions should match", "Hypertension", added.getPastConditions());
    }

    @Test
    public void testAddMedicalHistory_Null() {
        MedicalHistory added = medicalHistoryLogic.addMedicalHistory(null);
        assertNull("Added history should be null for null input", added);
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
}