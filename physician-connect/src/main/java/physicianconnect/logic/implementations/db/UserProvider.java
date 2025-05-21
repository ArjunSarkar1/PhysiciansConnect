package logic.implementations.db;

import logic.interfaces.db.UserRepository;
import objects.Patient;
import objects.Physician;

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