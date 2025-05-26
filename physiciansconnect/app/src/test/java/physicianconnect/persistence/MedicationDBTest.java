package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Medication;
import physicianconnect.persistence.sqlite.MedicationDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class MedicationDBTest {
    private Connection conn;
    private MedicationDB db;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new MedicationDB(conn);
    }

    @AfterEach
    public void cleanup() throws Exception {
        conn.close();
    }

    @Test
    public void testAddAndGetMedication() {
        Medication med = new Medication("Ibuprofen", "200mg");
        db.addMedication(med);

        List<Medication> all = db.getAllMedications();
        assertEquals(1, all.size());
        assertEquals("Ibuprofen", all.get(0).getName());
        assertEquals("200mg", all.get(0).getDosage());
    }

    @Test
    public void testDeleteSpecificMedication() {
        Medication med1 = new Medication("Ibuprofen", "200mg");
        Medication med2 = new Medication("Amoxicillin", "500mg");
        db.addMedication(med1);
        db.addMedication(med2);

        db.deleteMedication(med1);
        List<Medication> remaining = db.getAllMedications();

        assertEquals(1, remaining.size());
        assertEquals("Amoxicillin", remaining.get(0).getName());
    }

    @Test
    public void testDeleteAllMedications() {
        db.addMedication(new Medication("Tylenol", "500mg"));
        db.addMedication(new Medication("Advil", "400mg"));

        db.deleteAllMedications();
        List<Medication> after = db.getAllMedications();

        assertTrue(after.isEmpty());
    }

    @Test
    public void testGetAllMedicationsEmptyInitially() {
        List<Medication> empty = db.getAllMedications();
        assertNotNull(empty);
        assertTrue(empty.isEmpty());
    }
}
