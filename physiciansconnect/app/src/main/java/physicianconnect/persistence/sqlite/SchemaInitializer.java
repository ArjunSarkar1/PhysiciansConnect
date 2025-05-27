package physicianconnect.persistence.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    public static void initializeSchema(Connection connection) {
        String createPhysiciansTable = "CREATE TABLE IF NOT EXISTS physicians ("
                + "id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "email TEXT NOT NULL, "
                + "password TEXT NOT NULL"
                + ");";

        String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS appointments ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "physician_id TEXT NOT NULL, "
                + "patient_name TEXT NOT NULL, "
                + "datetime TEXT NOT NULL, "
                + "FOREIGN KEY (physician_id) REFERENCES physicians(id) ON DELETE CASCADE"
                + ");";

        String createMedicationsTable = "CREATE TABLE IF NOT EXISTS medications ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "dosage TEXT NOT NULL, "
                + "default_frequency TEXT, "
                + "default_notes TEXT"
                + ");";

        String createPrescriptionsTable = "CREATE TABLE IF NOT EXISTS prescriptions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "physician_id TEXT NOT NULL, "
                + "patient_name TEXT NOT NULL, "
                + "medication_name TEXT NOT NULL, "
                + "default_dosage TEXT NOT NULL, "
                + "dosage TEXT, "
                + "frequency TEXT, "
                + "notes TEXT, "
                + "date_prescribed TEXT NOT NULL, "
                + "FOREIGN KEY (physician_id) REFERENCES physicians(id) ON DELETE CASCADE"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createPhysiciansTable);
            stmt.execute(createAppointmentsTable);
            stmt.execute(createMedicationsTable);
            stmt.execute(createPrescriptionsTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize PhysicianConnect schema", e);
        }
    }
}