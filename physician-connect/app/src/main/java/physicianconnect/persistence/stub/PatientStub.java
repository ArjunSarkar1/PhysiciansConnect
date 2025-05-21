<<<<<<< HEAD:physician-connect/src/main/java/physicianconnect/persistence/Stub/PatientStub.java
package persistence.stub;

import objects.Patient;
=======
package physicianconnect.persistence.stub;
>>>>>>> 757cf7da1123155a4a972bfd438e6ec544ce05bd:physician-connect/app/src/main/java/physicianconnect/persistence/stub/PatientStub.java

import physicianconnect.objects.Patient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PatientStub {
    private final Map<Integer, Patient> patients = new HashMap<>();
    private int patientId = 1;

    private static final PatientStub instance = new PatientStub();

    private PatientStub() {
    }

    public static PatientStub getInstance() {
        return instance;
    }

    public Patient getPatient(int patientId) {
        Patient toReturn = this.patients.get(patientId);
        Patient result = null;
        if (toReturn != null) {
            result = createCopy(toReturn.getUserId(), toReturn);
        }
        return result;
    }

    public Map<Integer, Patient> getAllPatients() {
        return Collections.unmodifiableMap(this.patients);
    }

    public Patient addPatient(Patient patient) {
        Patient toAdd = createCopy(this.patientId++, patient);
        this.patients.put(toAdd.getUserId(), toAdd);
        return toAdd;
    }

    public Patient updatePatient(Patient toUpdate) {
        Patient updatedPatient = null;
        if (patients.containsKey(toUpdate.getUserId())) {
            updatedPatient = createCopy(toUpdate.getUserId(), toUpdate);
            patients.put(updatedPatient.getUserId(), updatedPatient);
        }
        return updatedPatient;
    }

    public Patient deletePatient(int patientId){
        Patient toDelete = patients.get(patientId);
        Patient result = null;
        if(toDelete != null){
            result = createCopy(toDelete.getUserId(), toDelete);
            this.patients.remove(patientId);
        }
        return result;
    }
    private Patient createCopy(int copyId, Patient toCopy) {
        return new Patient(
                copyId,
                toCopy.getFirstName(),
                toCopy.getLastName(),
                toCopy.getEmail(),
                toCopy.getSIN(),
                toCopy.getPHIN(),
                null,
                toCopy.getMedicalHistory());
    }
}
