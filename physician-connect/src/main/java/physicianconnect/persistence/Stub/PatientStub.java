import physicianconnect.objects.Patient;
import physicianconnect.object.MedicalHistory;

import java.util.*;

public class PatientStub{
    private final Map<Integer, User> patients = new HashMap<>();
    private int patientId = 1;

    private static final PatientStub instance = new PatientStub();

    private PatientStub(){}

    public static PatientStub getInstance(){
        return this.instance;
    }

    public Patient
}



