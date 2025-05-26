package physicianconnect.persistence.stub;

import physicianconnect.persistence.AppointmentPersistence;
import physicianconnect.persistence.MedicationPersistence;
import physicianconnect.persistence.PhysicianPersistence;

public class StubFactory {

    public static PhysicianPersistence createPhysicianPersistence() {
        return new PhysicianPersistenceStub(true); // seeded
    }

    public static AppointmentPersistence createAppointmentPersistence() {
        return new AppointmentPersistenceStub(true); // seeded
    }

    public static MedicationPersistence createMedicationPersistence() {
        return new MedicationPersistenceStub(true); // seeded
    }
}
