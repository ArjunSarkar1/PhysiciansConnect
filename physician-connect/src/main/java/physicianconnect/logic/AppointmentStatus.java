package logic;

/**
 * The current state of an appointment.
 */
public enum AppointmentStatus {
    PENDING, // newly created, awaiting confirmation
    CONFIRMED, // physician/patient have agreed on time
    CANCELLED, // either side has cancelled
    COMPLETED // appointment has taken place
}