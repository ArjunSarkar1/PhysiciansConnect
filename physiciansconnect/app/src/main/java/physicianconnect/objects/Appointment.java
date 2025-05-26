package physicianconnect.objects;

import java.time.LocalDateTime;

public class Appointment {
    private final String physicianId;
    private final String patientName;
    private final LocalDateTime dateTime;

    public Appointment(String physicianId, String patientName, LocalDateTime dateTime) {
        this.physicianId = physicianId;
        this.patientName = patientName;
        this.dateTime = dateTime;
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

    @Override
    public String toString() {
        return "Appointment with " + patientName + " on " + dateTime;
    }
}
