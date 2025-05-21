package physicianconnect.objects;

import java.util.Collections;
import java.util.List;

public class Patient extends User {

    private String SIN;
    private String PHIN;
    private List<MedicalHistory> medicalHistory;

    public Patient(
            int id, String firstName, String lastName, String email, String SIN, String PHIN,
            List<Appointment> appointmentsList, List<MedicalHistory> medicalHistory) {

        super(id, firstName, lastName, email);
        this.SIN = SIN;
        this.PHIN = PHIN;
        if (medicalHistory != null) {
            this.medicalHistory.addAll(medicalHistory);
        }
    }

    // Getters and Setters
    public String getSIN() {
        return this.SIN;
    }

    public void setSIN(String SIN) {
        this.SIN = SIN;
    }

    public String getPHIN() {
        return this.PHIN;
    }

    public void setPHIN(String PHIN) {
        this.PHIN = PHIN;
    }

    public List<MedicalHistory> getMedicalHistory() {
        return Collections.unmodifiableList(medicalHistory);
    }

    public void addMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory.add(medicalHistory);
    }
}
