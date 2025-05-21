package logic.stub;

import persistence.stub.*;
import objects.Patient;
import java.util.*;

public class PatientLogic {
    private static final PatientStub tempDB = PatientStub.getInstance();

    public Patient getPatientById(int patientId) {
        Patient result = null;
        try {
            if (patientId > 0) {
                result = tempDB.getPatient(patientId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Map<Integer, Patient> getAllPatients() {
        return tempDB.getAllPatients();
    }

    public Patient addPatient(Patient patient) {
        Patient result = null;
        try {
            if (patient != null) {
                result = tempDB.addPatient(patient);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Patient updatePatient(Patient patient) {
        Patient result = null;
        try {
            if (patient != null && patient.getUserId() > 0) {
                result = tempDB.updatePatient(patient);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Patient deletePatient(int patientId) {
        Patient result = null;
        try {
            if (patientId > 0) {
                result = tempDB.deletePatient(patientId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
} 