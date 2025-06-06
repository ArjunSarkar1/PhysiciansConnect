package physicianconnect.logic.controller;

import java.time.LocalDateTime;
import java.util.List;

import physicianconnect.logic.exceptions.InvalidAppointmentException;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;

/**
 * Controller for Appointment use‐cases.
 * Delegates persistence/validation to AppointmentManager.
 */
public class AppointmentController {
    private final AppointmentManager appointmentManager;

    public AppointmentController(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
    }

    /**
     * Create and persist a new appointment.
     *
     * @param physicianId the ID of the physician
     * @param patientName the name of the patient
     * @param dateTime    the desired appointment date & time
     * @param notes       any optional notes (may be null or empty)
     * @throws InvalidAppointmentException if validation or slot‐conflict occurs
     */
    public void createAppointment(
            String physicianId,
            String patientName,
            LocalDateTime dateTime,
            String notes
    ) throws InvalidAppointmentException {
        // Build a new Appointment object
        Appointment appt = new Appointment(physicianId, patientName, dateTime);
        if (notes != null && !notes.trim().isEmpty()) {
            appt.setNotes(notes.trim());
        }
        // Delegate to manager, which will validate + persist
        appointmentManager.addAppointment(appt);
    }

    /**
     * Update the notes on an existing appointment.
     *
     * @param appt     the Appointment object to modify
     * @param newNotes the new notes string
     * @throws InvalidAppointmentException if manager’s validation fails
     */
    public void updateAppointmentNotes(
            Appointment appt,
            String newNotes
    ) throws InvalidAppointmentException {
        // Update the in‐memory object
        appt.setNotes(newNotes == null ? "" : newNotes.trim());
        // Delegate to manager, which validates slot availability and persists
        appointmentManager.updateAppointment(appt);
    }

    /**
     * Delete an existing appointment.
     *
     * @param appt the Appointment to remove
     */
    public void deleteAppointment(Appointment appt) {
        appointmentManager.deleteAppointment(appt);
    }

    /**
     * Fetch all appointments for a given physician.
     *
     * @param physicianId the physician’s ID
     * @return an unmodifiable List of Appointment objects
     */
    public List<Appointment> getAppointmentsForPhysician(String physicianId) {
        return appointmentManager.getAppointmentsForPhysician(physicianId);
    }
}
