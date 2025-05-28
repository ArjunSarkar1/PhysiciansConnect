package physicianconnect.logic;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.interfaces.AppointmentPersistence;

import java.util.Collections;
import java.util.List;

public class AppointmentManager {

    private final AppointmentPersistence appointmentDB;

    public AppointmentManager(AppointmentPersistence appointmentDB) {
        this.appointmentDB = appointmentDB;
    }

    public void addAppointment(Appointment appointment) {
        AppointmentValidator.validate(appointment);
        appointmentDB.addAppointment(appointment);
    }

    public void deleteAppointment(Appointment appointment) {
        appointmentDB.deleteAppointment(appointment);
    }

    public List<Appointment> getAppointmentsForPhysician(String physicianId) {
        return Collections.unmodifiableList(appointmentDB.getAppointmentsForPhysician(physicianId));
    }

    public void deleteAll() {
        appointmentDB.deleteAllAppointments();
    }
}
