package physicianconnect.logic.interfaces.ui;

import java.io.OutputStream;
import physicianconnect.objects.Appointment;

/**
 * Service interface for exporting appointment summaries.
 */
public interface AppointmentSummaryService {

    /**
     * Exports a summary of the given appointment to the provided output stream.
     * <p>
     * The summary could include details such as the physician's name, patient
     * information,
     * reason for visit, diagnosis, prescribed medications, and visit notes.
     * <p>
     * The output format (e.g., PDF, plain text) depends on the implementation.
     *
     * @param appointment the {@link Appointment} to summarize and export
     * @param out         the {@link OutputStream} to which the summary will be
     *                    written
     */
    void exportAppointmentSummary(Appointment appointment, OutputStream out);
}
