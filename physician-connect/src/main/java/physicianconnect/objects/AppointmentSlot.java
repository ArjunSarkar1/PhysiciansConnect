package objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppointmentSlot {

    private int slotId;
    private int physicianId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
    private List<Appointment> appointmentsList = new ArrayList<>();

    public AppointmentSlot(int slotId, int physicianId, LocalDateTime startTime, LocalDateTime endTime,
            boolean isAvailable, List<Appointment> appointmentsList) {
        this.slotId = slotId;
        this.physicianId = physicianId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
        if (appointmentsList != null) {
            this.appointmentsList.addAll(appointmentsList);
        }
    }

    // Getters and Setters
    public int getSlotId() {
        return this.slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getPhysicianId() {
        return this.physicianId;
    }

    public void setPhysicianId(int physicianId) {
        this.physicianId = physicianId;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Appointment> getAllAppointments() {
        return Collections.unmodifiableList(this.appointmentsList);
    }

    public void addAppointment(Appointment appointment) {
        // TODO: Check if it will work with other appointment time wise
        this.appointmentsList.add(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        this.appointmentsList.remove(appointment);
    }

    public int getNumberOfAppointemntsBooked() {
        return this.appointmentsList.size();
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    private boolean overlapsWith(AppointmentSlot other) {
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }
}
