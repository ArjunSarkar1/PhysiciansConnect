package objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Physician extends User {

    private int physicianId;
    private int officeId;
    ArrayList<Appointment> appointmentsList = new ArrayList<>();

    Physician(int id, String firstName, String lastName, String email, int physicianId, int officeId) {

        super(id, firstName, lastName, email);
        this.physicianId = physicianId;
        this.officeId = officeId;
    }

    // Optionally include a constructor that accepts appointments
    Physician(int id, String firstName, String lastName, String email, int physicianId, int officeId,
            List<Appointment> appointmentsList) {

        super(id, firstName, lastName, email);
        this.physicianId = physicianId;
        this.officeId = officeId;
        if (appointmentsList != null) {
            this.appointmentsList.addAll(appointmentsList);
        }
    }

    // Getters and Setters
    public int getPhysicianId() {
        return physicianId;
    }

    public void setPhysicianId(int physicianId) {
        this.physicianId = physicianId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
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
}
