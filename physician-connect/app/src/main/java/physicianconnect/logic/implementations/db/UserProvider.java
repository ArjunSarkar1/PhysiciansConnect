package physicianconnect.logic.implementations.db;

import physicianconnect.logic.interfaces.db.UserRepository;
import physicianconnect.objects.Patient;
import physicianconnect.objects.Physician;

public class UserProvider implements UserRepository {

    @Override
    public Patient getPatientByEmailAndPassword(String patientEmail, String patientPassowrd) {
        throw new UnsupportedOperationException("Unimplemented method 'getPatientByEmailAndPassword'");
    }

    @Override
    public Physician getPhysicianByEmailAndPassword(String physicianEmail, String physicianPassowrd) {
        throw new UnsupportedOperationException("Unimplemented method 'getPhysicianByEmailAndPassword'");
    }

}