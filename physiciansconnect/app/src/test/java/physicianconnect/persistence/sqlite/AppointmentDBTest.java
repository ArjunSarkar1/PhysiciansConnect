package physicianconnect.persistence.sqlite;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Appointment;

public class AppointmentDBTest {

    private Connection conn;
    private AppointmentDB db;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);
        db = new AppointmentDB(conn);
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testAddAndFetchAppointment() {
        Appointment a = new Appointment("doc1", "Bruce Banner", LocalDateTime.now());
        db.addAppointment(a);

        List<Appointment> list = db.getAppointmentsForPhysician("doc1");
        assertEquals(1, list.size());
        assertEquals("Bruce Banner", list.get(0).getPatientName());
    }

    @Test
    public void testNoAppointmentsForUnknownPhysician() {
        List<Appointment> result = db.getAppointmentsForPhysician("unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteAppointment() {
        Appointment a = new Appointment("doc1", "Delete Me", LocalDateTime.now());
        db.addAppointment(a);
        db.deleteAppointment(a);
        List<Appointment> list = db.getAppointmentsForPhysician("doc1");
        assertTrue(list.stream().noneMatch(appt -> appt.getPatientName().equals("Delete Me")));
    }

    @Test
    public void testDeleteAllAppointments() {
        db.addAppointment(new Appointment("doc1", "A", LocalDateTime.now()));
        db.addAppointment(new Appointment("doc2", "B", LocalDateTime.now()));
        db.deleteAllAppointments();
        assertTrue(db.getAppointmentsForPhysician("doc1").isEmpty());
        assertTrue(db.getAppointmentsForPhysician("doc2").isEmpty());
    }

    @Test
    public void testAddAppointmentCatchesSQLException() {
        Appointment a = new Appointment(null, null, null);
        assertThrows(RuntimeException.class, () -> db.addAppointment(a));
    }
}