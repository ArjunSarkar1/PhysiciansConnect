package physicianconnect.persistence.sqlite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import physicianconnect.objects.Medication;

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
        Medication med = new Medication("Ibuprofen", "200mg", "Twice a day", "Take with food");
        db.addMedication(med);

        List<Medication> all = db.getAllMedications();
        assertEquals(1, all.size());
        assertEquals("Ibuprofen", all.get(0).getName());
        assertEquals("200mg", all.get(0).getDosage());
        assertEquals("Twice a day", all.get(0).getDefaultFrequency());
        assertEquals("Take with food", all.get(0).getDefaultNotes());
    }

    @Test
    public void testDeleteSpecificMedication() {
        Medication med1 = new Medication("Ibuprofen", "200mg", "Twice a day", "Take with food");
        Medication med2 = new Medication("Amoxicillin", "500mg", "Three times a day", "Finish all medication");
        db.addMedication(med1);
        db.addMedication(med2);

        db.deleteMedication(med1);
        List<Medication> remaining = db.getAllMedications();

        assertEquals(1, remaining.size());
        assertEquals("Amoxicillin", remaining.get(0).getName());
        assertEquals("Three times a day", remaining.get(0).getDefaultFrequency());
        assertEquals("Finish all medication", remaining.get(0).getDefaultNotes());
    }

    @Test
    public void testDeleteAllMedications() {
        db.addMedication(new Medication("Tylenol", "500mg", "Once a day", "No alcohol"));
        db.addMedication(new Medication("Advil", "400mg", "Once a day", "Take with water"));

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