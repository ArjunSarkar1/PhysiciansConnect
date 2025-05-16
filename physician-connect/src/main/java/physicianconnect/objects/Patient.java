package objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Patient extends User {

    private String SIN;
    private String PHIN;
    private List<Appointment> appointmentsList = new ArrayList<>();
    private List<MedicalHistory> medicalHistory;

    public Patient(
            int id, String firstName, String lastName, String email, String SIN, String PHIN,
            List<Appointment> appointmentsList, List<MedicalHistory> medicalHistory) {

        super(id, firstName, lastName, email);
        this.SIN = SIN;
        this.PHIN = PHIN;
        if (appointmentsList != null) {
            this.appointmentsList.addAll(appointmentsList);
        }
        if (medicalHistory != null) {
            this.medicalHistory.addAll(medicalHistory);
        }
    }

    // Getters and Setters
    public String getSIN() {
        return SIN;
    }

    public void setSIN(String SIN) {
        this.SIN = SIN;
    }

    public String getPHIN() {
        return PHIN;
    }

    public void setPHIN(String PHIN) {
        this.PHIN = PHIN;
    }

    public List<Appointment> getAppointmentsList() {
        return Collections.unmodifiableList(appointmentsList);
    }

    public void addAppointment(Appointment appointment) {
        this.appointmentsList.add(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        this.appointmentsList.remove(appointment);
    }

    public List<MedicalHistory> getMedicalHistory() {
        return Collections.unmodifiableList(medicalHistory);
    }

    public void addMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory.add(medicalHistory);
    }
}
