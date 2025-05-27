package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Prescription;
import physicianconnect.persistence.sqlite.PrescriptionDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class PrescriptionDBTest {

    private Connection conn;
    private PrescriptionDB db;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new PrescriptionDB(conn);
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndFetchPrescription() {
        Prescription p = new Prescription(
            0, "doc1", "Bruce Banner", "Ibuprofen", "200mg", "200mg", "Once a day", "Take with food", "2025-06-01T10:00"
        );
        db.addPrescription(p);

        List<Prescription> list = db.getPrescriptionsForPatient("Bruce Banner");
        assertEquals(1, list.size());
        Prescription fetched = list.get(0);
        assertEquals("Ibuprofen", fetched.getMedicationName());
        assertEquals("doc1", fetched.getPhysicianId());
        assertEquals("Once a day", fetched.getFrequency());
    }

    @Test
    public void testDeletePrescriptionById() {
        Prescription p = new Prescription(
            0, "doc2", "Tony Stark", "Amoxicillin", "500mg", "500mg", "Twice a day", "No alcohol", "2025-06-02T09:00"
        );
        db.addPrescription(p);
        List<Prescription> list = db.getPrescriptionsForPatient("Tony Stark");
        assertEquals(1, list.size());
        int id = list.get(0).getId();

        db.deletePrescriptionById(id);
        List<Prescription> afterDelete = db.getPrescriptionsForPatient("Tony Stark");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testDeleteAllPrescriptions() {
        db.addPrescription(new Prescription(0, "doc1", "A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00"));
        db.addPrescription(new Prescription(0, "doc2", "B", "Amoxicillin", "500mg", "500mg", "Twice", "", "2025-06-02T09:00"));
        db.deleteAllPrescriptions();
        List<Prescription> all = db.getAllPrescriptions();
        assertTrue(all.isEmpty());
    }

    @Test
    public void testGetAllPrescriptionsResultSetMapping() {
        db.addPrescription(new Prescription(0, "doc1", "A", "Ibuprofen", "200mg", "200mg", "Once", "Note1", "2025-06-01T10:00"));
        db.addPrescription(new Prescription(0, "doc2", "B", "Amoxicillin", "500mg", "500mg", "Twice", "Note2", "2025-06-02T09:00"));
        List<Prescription> all = db.getAllPrescriptions();
        assertEquals(2, all.size());
        assertEquals("Ibuprofen", all.get(0).getMedicationName());
        assertEquals("Amoxicillin", all.get(1).getMedicationName());
    }

    @Test
    public void testAddPrescriptionCatchesSQLException() {
        Prescription p = new Prescription(0, null, null, null, null, null, null, null, null);
        assertThrows(RuntimeException.class, () -> db.addPrescription(p));
    }
}