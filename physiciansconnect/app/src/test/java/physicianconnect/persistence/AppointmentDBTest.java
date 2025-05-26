package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;

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



}
