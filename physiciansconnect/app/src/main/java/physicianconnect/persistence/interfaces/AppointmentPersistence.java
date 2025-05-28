package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Appointment;

import java.util.List;

public interface AppointmentPersistence {
    void addAppointment(Appointment appointment);

    void updateAppointment(Appointment appointment);

    void deleteAppointment(Appointment appointment);

    void deleteAllAppointments();

    List<Appointment> getAppointmentsForPhysician(String physicianId);
}
