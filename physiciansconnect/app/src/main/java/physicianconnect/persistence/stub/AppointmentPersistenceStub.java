package physicianconnect.persistence.stub;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.AppointmentPersistence;

import java.util.*;

public class AppointmentPersistenceStub implements AppointmentPersistence {
    private final List<Appointment> appointments;

    public AppointmentPersistenceStub(boolean seed) {
        appointments = new ArrayList<>();
        if (seed) {
            appointments.add(new Appointment("1", "Alice Johnson", java.time.LocalDateTime.of(2025, 5, 30, 10, 0)));
            appointments.add(new Appointment("2", "Bob Brown", java.time.LocalDateTime.of(2025, 6, 1, 14, 30)));
        }
    }

    @Override
    public List<Appointment> getAppointmentsForPhysician(String physicianId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appt : appointments) {
            if (appt.getPhysicianId().equals(physicianId)) {
                result.add(appt);
            }
        }
        return result;
    }

    public void addAppointment(Appointment appointment) {
        if (appointment != null) {
            appointments.add(appointment);
        }
    }

    public void deleteAppointment(Appointment appointment) {
        appointments.remove(appointment);
    }

    public void deleteAllAppointments() {
        appointments.clear();
    }

    public void close() {
        appointments.clear();
    }
}
