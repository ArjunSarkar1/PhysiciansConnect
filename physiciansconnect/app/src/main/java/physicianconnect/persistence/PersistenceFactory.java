package physicianconnect.persistence;

import java.sql.Connection;
import java.util.List;

import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.persistence.sqlite.MedicationDB;
import physicianconnect.persistence.sqlite.PhysicianDB;
import physicianconnect.persistence.sqlite.SchemaInitializer;
import physicianconnect.persistence.stub.StubFactory;

public class PersistenceFactory {

    private static PhysicianPersistence physicianPersistence;
    private static AppointmentPersistence appointmentPersistence;
    private static MedicationPersistence medicationPersistence;

    public static void initialize(PersistenceType type, boolean seed) {
        if (physicianPersistence != null || appointmentPersistence != null || medicationPersistence != null)
            return;

        switch (type) {
            case PROD, TEST -> {
                // Store in root dir like your teacher's version
                String dbPath = type == PersistenceType.PROD ? "prod.db" : "test.db";
                try {
                    ConnectionManager.initialize(dbPath);
                    Connection conn = ConnectionManager.get();

                    SchemaInitializer.initializeSchema(conn);

                    if (seed) {
                        DatabaseSeeder.seed(conn, List.of(
                                "seed_physicians.sql",
                                "seed_appointments.sql",
                                "seed_medications.sql"));
                    }

                    physicianPersistence = new PhysicianDB(conn);
                    appointmentPersistence = new AppointmentDB(conn);
                    medicationPersistence = new MedicationDB(conn);

                } catch (Exception e) {
                    fallbackToStubs(e);
                }
            }
            case STUB -> fallbackToStubs(null);
        }
    }

    private static void fallbackToStubs(Exception e) {
        physicianPersistence = StubFactory.createPhysicianPersistence();
        appointmentPersistence = StubFactory.createAppointmentPersistence();
        medicationPersistence = StubFactory.createMedicationPersistence();

        if (e != null) {
            System.err.println("Falling back to stubs due to: " + e.getMessage());
        }
    }

    public static PhysicianPersistence getPhysicianPersistence() {
        return physicianPersistence;
    }

    public static AppointmentPersistence getAppointmentPersistence() {
        return appointmentPersistence;
    }

    public static MedicationPersistence getMedicationPersistence() {
        return medicationPersistence;
    }

    public static void reset() {
        ConnectionManager.close();
        physicianPersistence = null;
        appointmentPersistence = null;
        medicationPersistence = null;
    }
}
