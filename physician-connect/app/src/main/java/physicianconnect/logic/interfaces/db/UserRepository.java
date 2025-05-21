package physicianconnect.logic.interfaces.db;

import physicianconnect.objects.Patient;
import physicianconnect.objects.Physician;

/**
 * Repository interface for retrieving user accounts, including patients and
 * physicians,
 * based on login credentials.
 */
public interface UserRepository {

    /**
     * Retrieves a patient account using the provided email and password
     * credentials.
     * <p>
     * Typically used for authentication during patient login.
     *
     * @param patientEmail    the patient's email address
     * @param patientPassowrd the patient's password
     * @return the matching {@link Patient} object
     */
    Patient getPatientByEmailAndPassword(String patientEmail, String patientPassowrd);

    /**
     * Retrieves a physician account using the provided email and password
     * credentials.
     * <p>
     * Typically used for authentication during physician login.
     *
     * @param physicianEmail    the physician's email address
     * @param physicianPassowrd the physician's password
     * @return the matching {@link Physician} object
     */
    Physician getPhysicianByEmailAndPassword(String physicianEmail, String physicianPassowrd);
}