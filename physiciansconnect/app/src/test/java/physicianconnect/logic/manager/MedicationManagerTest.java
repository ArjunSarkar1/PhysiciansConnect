package physicianconnect.logic.manager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Medication;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;
import physicianconnect.persistence.interfaces.MedicationPersistence;

public class MedicationManagerTest {

    private MedicationPersistence medDB;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialize(PersistenceType.TEST, false);
        medDB = PersistenceFactory.getMedicationPersistence();
    }

    @AfterEach
    public void teardown() {
        PersistenceFactory.reset();
    }

    @Test
    public void testAddAndFetchMedication() {
        Medication m = new Medication("Ibuprofen", "200mg", "Twice a day", "Take with food");
        medDB.addMedication(m);

        List<Medication> result = medDB.getAllMedications();

        boolean found = result.stream()
                .anyMatch(med -> med.getName().equals("Ibuprofen")
                        && med.getDosage().equals("200mg")
                        && "Twice a day".equals(med.getDefaultFrequency())
                        && "Take with food".equals(med.getDefaultNotes()));

        assertTrue(found, "Expected to find medication 'Ibuprofen' with dosage '200mg', frequency 'Twice a day', and notes 'Take with food'");
    }

    @Test
    public void testDeleteMedication() {
        Medication m = new Medication("Paracetamol", "500mg", "Once a day", "No alcohol");
        medDB.addMedication(m);

        // Ensure it was added
        List<Medication> beforeDelete = medDB.getAllMedications();
        boolean wasAdded = beforeDelete.stream()
                .anyMatch(med -> med.getName().equals("Paracetamol")
                        && med.getDosage().equals("500mg")
                        && "Once a day".equals(med.getDefaultFrequency())
                        && "No alcohol".equals(med.getDefaultNotes()));
        assertTrue(wasAdded, "Medication should be added before deletion");

        // Delete and verify it's gone
        medDB.deleteMedication(m);
        List<Medication> afterDelete = medDB.getAllMedications();
        boolean stillExists = afterDelete.stream()
                .anyMatch(med -> med.getName().equals("Paracetamol")
                        && med.getDosage().equals("500mg")
                        && "Once a day".equals(med.getDefaultFrequency())
                        && "No alcohol".equals(med.getDefaultNotes()));
        assertFalse(stillExists, "Medication should be deleted and not found in the list");
    }
}