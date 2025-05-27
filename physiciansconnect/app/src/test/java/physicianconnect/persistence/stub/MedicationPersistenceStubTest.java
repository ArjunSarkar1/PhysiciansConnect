package physicianconnect.persistence.stub;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Medication;

import static org.junit.jupiter.api.Assertions.*;

public class MedicationPersistenceStubTest {

    private MedicationPersistenceStub stub;

    @BeforeEach
    public void setup() {
        stub = new MedicationPersistenceStub(true);
    }

    @Test
    public void testAddAndGetMedication() {
        Medication m = new Medication("TestMed", "123mg");
        stub.addMedication(m);
        assertTrue(stub.getAllMedications().stream().anyMatch(med -> med.getName().equals("TestMed")));
    }

    @Test
    public void testDeleteMedication() {
        Medication m = new Medication("TestMed", "123mg");
        stub.addMedication(m);
        stub.deleteMedication(m);
        assertFalse(stub.getAllMedications().stream().anyMatch(med -> med.getName().equals("TestMed")));
    }

    @Test
    public void testDeleteAllMedications() {
        stub.deleteAllMedications();
        assertTrue(stub.getAllMedications().isEmpty());
    }
}