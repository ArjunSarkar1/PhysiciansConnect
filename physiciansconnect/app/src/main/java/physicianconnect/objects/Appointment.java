package physicianconnect.objects;

import java.time.LocalDateTime;

public class Appointment {
    private final String physicianId;
    private final String patientName;
    private final LocalDateTime dateTime;
    private String notes;

    public Appointment(String physicianId, String patientName, LocalDateTime dateTime) {
        this.physicianId = physicianId;
        this.patientName = patientName;
        this.dateTime = dateTime;
        this.notes = "";
    }

    public Appointment(String physicianId, String patientName, LocalDateTime dateTime, String notes) {
        this.physicianId = physicianId;
        this.patientName = patientName;
        this.dateTime = dateTime;
        this.notes = notes;
    }

    public String getPhysicianId() {
        return physicianId;
    }

    public String getPatientName() {
        return patientName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Being used to in dashboard
    @Override
    public String toString() {
        return "Appointment with " + patientName + " on " + 
               dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
    }
}
