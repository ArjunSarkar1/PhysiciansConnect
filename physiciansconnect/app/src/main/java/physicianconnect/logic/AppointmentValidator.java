package physicianconnect.logic;

import physicianconnect.exceptions.InvalidAppointmentException;
import physicianconnect.objects.Appointment;

public class AppointmentValidator {

    public static void validate(Appointment appointment) {
        if (appointment == null) {
            throw new InvalidAppointmentException("Appointment cannot be null.");
        }

        if (appointment.getPhysicianId() == null || appointment.getPhysicianId().isBlank()) {
            throw new InvalidAppointmentException("Physician ID is required.");
        }

        if (appointment.getPatientName() == null || appointment.getPatientName().isBlank()) {
            throw new InvalidAppointmentException("Patient name is required.");
        }

        if (appointment.getDateTime() == null) {
            throw new InvalidAppointmentException("Appointment date and time is required.");
        }
    }
}
