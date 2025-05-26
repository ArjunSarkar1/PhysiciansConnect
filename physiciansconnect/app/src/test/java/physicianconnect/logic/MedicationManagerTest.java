package physicianconnect.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Medication;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.PersistenceType;
import physicianconnect.persistence.MedicationPersistence;

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
        Medication m = new Medication("Ibuprofen", "200mg");
        medDB.addMedication(m);

        List<Medication> result = medDB.getAllMedications();
        assertEquals(1, result.size());
        assertEquals("Ibuprofen", result.get(0).getName());
    }

    @Test
    public void testDeleteMedication() {
        Medication m = new Medication("Paracetamol", "500mg");
        medDB.addMedication(m);
        medDB.deleteMedication(m);

        List<Medication> meds = medDB.getAllMedications();
        assertTrue(meds.isEmpty());
    }
}
