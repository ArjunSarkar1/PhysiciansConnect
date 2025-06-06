package physicianconnect.persistence.stub;

import physicianconnect.persistence.interfaces.*;

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

    public static ReferralPersistence createReferralPersistence() {
        return new ReferralPersistenceStub(true); // seeded
    }

    public static ReceptionistPersistence createReceptionistPersistence() {
        return new ReceptionistPersistenceStub(true); // seeded
    }

    public static InvoicePersistence createInvoicePersistence() {
        return new InvoicePersistenceStub(true); // seeded
    }

    public static PaymentPersistence createPaymentPersistence() {
        return new PaymentPersistenceStub(true); // seeded
    }
}