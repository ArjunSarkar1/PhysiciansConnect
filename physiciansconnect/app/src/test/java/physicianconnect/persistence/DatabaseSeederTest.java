package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import physicianconnect.persistence.sqlite.SchemaInitializer;

public class DatabaseSeederTest {

    private Connection conn;

    @BeforeEach
    public void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        conn.setAutoCommit(false);

        SchemaInitializer.initializeSchema(conn);

        // Seed test files must exist under /src/test/resources/
        DatabaseSeeder.seed(conn, List.of(
                "seed_physicians.sql",
                "seed_appointments.sql",
                "seed_medications.sql",
                "seed_prescriptions.sql"
                ));
    }

    @AfterEach
    public void teardown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.rollback(); // undo all changes
            conn.close();
        }
    }

    @Test
    public void testPhysicianSeeded() throws Exception {
        assertNotNull(DatabaseSeeder.class.getClassLoader().getResourceAsStream("seed_physicians.sql"));

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM physicians")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) >= 1, "Should have seeded at least one physician");
        }
    }

    @Test
    public void testAppointmentSeeded() throws Exception {
        assertNotNull(DatabaseSeeder.class.getClassLoader().getResourceAsStream("seed_appointments.sql"));

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM appointments")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) >= 1, "Should have seeded at least one appointment");
        }
    }

    @Test
    public void testMedicationSeeded() throws Exception {
        assertNotNull(DatabaseSeeder.class.getClassLoader().getResourceAsStream("seed_medications.sql"));

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM medications")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) >= 1, "Should have seeded at least one medication");
        }
    }

    @Test
    public void testPrescriptionSeeded() throws Exception {
        assertNotNull(DatabaseSeeder.class.getClassLoader().getResourceAsStream("seed_prescriptions.sql"));

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM prescriptions")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) >= 1, "Should have seeded at least one prescription");
        }
    }
}
