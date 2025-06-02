package physicianconnect.logic;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.interfaces.AppointmentPersistence;

import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;

public class AppointmentManager {

    private final AppointmentPersistence appointmentDB;

    public AppointmentManager(AppointmentPersistence appointmentDB) {
        this.appointmentDB = appointmentDB;
    }

    public void addAppointment(Appointment appointment) {
        AppointmentValidator.validate(appointment);
        appointmentDB.addAppointment(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        AppointmentValidator.validate(appointment);
        appointmentDB.updateAppointment(appointment);
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
    /**
     * Returns true if no appointment exists for this physician at exactly slotTime.
     */
    public boolean isSlotAvailable(String physicianId, LocalDateTime slotTime) {
        for (Appointment a : getAppointmentsForPhysician(physicianId)) {
            if (a.getDateTime().equals(slotTime)) {
                return false;
            }
        }
        return true;
    }
}
