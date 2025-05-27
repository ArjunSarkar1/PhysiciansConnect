package physicianconnect.persistence.stub;

import physicianconnect.persistence.AppointmentPersistence;
import physicianconnect.persistence.MedicationPersistence;
import physicianconnect.persistence.PhysicianPersistence;
import physicianconnect.persistence.PrescriptionPersistence;

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
    
    public static PrescriptionPersistence createPrescriptionPersistence() {
        return new PrescriptionPersistenceStub(true); // seeded
    }
}