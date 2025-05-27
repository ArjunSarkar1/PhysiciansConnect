package physicianconnect.persistence;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import physicianconnect.persistence.sqlite.SchemaInitializer;

public class SchemaTest {

    @Test
    public void testSchemaInitializer_createsPhysiciansTable() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='physicians'");
        assertTrue(rs.next(), "Table 'physicians' should exist");
    }

    @Test
    public void testSchemaInitializer_createsAppointmentsTable() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='appointments'");
        assertTrue(rs.next(), "Table 'appointments' should exist");
    }

    @Test
    public void testSchemaInitializer_createsMedicationsTable() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='medications'");
        assertTrue(rs.next(), "Table 'medications' should exist");
    }

    @Test
    public void testSchemaInitializer_createsPrescriptionsTable() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        SchemaInitializer.initializeSchema(conn);

        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='prescriptions'");
        assertTrue(rs.next(), "Table 'prescriptions' should exist");
    }
}
