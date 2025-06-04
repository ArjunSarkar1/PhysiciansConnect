package physicianconnect.logic;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.interfaces.AppointmentPersistence;
import physicianconnect.logic.exceptions.*;
import physicianconnect.logic.validation.*;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class AppointmentManager {

    private final AppointmentPersistence appointmentDB;
    private final Clock clock;   // ← we inject a clock here

    /** Production‐ready constructor */
    public AppointmentManager(AppointmentPersistence appointmentDB) {
        this(appointmentDB, Clock.systemDefaultZone());
    }

    /** Full constructor (allows tests to pass in a fixed Clock) */
    public AppointmentManager(AppointmentPersistence appointmentDB, Clock clock) {
        this.appointmentDB = appointmentDB;
        this.clock = clock;
    }

    public void addAppointment(Appointment appointment) {
        // ← use validate(appointment, clock) so “now” is correct
        AppointmentValidator.validate(appointment, clock);

        if (!isSlotAvailable(appointment.getPhysicianId(), appointment.getDateTime())) {
            throw new InvalidAppointmentException(
                    "Slot already taken at " + appointment.getDateTime()
            );
        }
        appointmentDB.addAppointment(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        AppointmentValidator.validate(appointment, clock);
        if (!isSlotAvailableForUpdate(
                appointment.getPhysicianId(),
                appointment.getDateTime(),
                appointment
        )) {
            throw new InvalidAppointmentException(
                    "Cannot move to " + appointment.getDateTime() + " — slot occupied."
            );
        }
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

    public boolean isSlotAvailable(String physicianId, LocalDateTime slotTime) {
        for (Appointment a : getAppointmentsForPhysician(physicianId)) {
            if (a.getDateTime().equals(slotTime)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSlotAvailableForUpdate(String physicianId,
                                            LocalDateTime slotTime,
                                            Appointment original) {
        for (Appointment other : getAppointmentsForPhysician(physicianId)) {
            boolean sameRecord =
                    other.getPhysicianId().equals(original.getPhysicianId())
                            && other.getPatientName().equals(original.getPatientName())
                            && other.getDateTime().equals(original.getDateTime());
            if (sameRecord) {
                continue;
            }
            if (other.getDateTime().equals(slotTime)) {
                return false;
            }
        }
        return true;
    }
}