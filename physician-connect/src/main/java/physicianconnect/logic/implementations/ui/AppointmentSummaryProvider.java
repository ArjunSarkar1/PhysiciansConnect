package logic.implementations.ui;

import java.io.OutputStream;
import logic.interfaces.ui.AppointmentSummaryService;
import objects.Appointment;

public class AppointmentSummaryProvider implements AppointmentSummaryService {

    @Override
    public void exportAppointmentSummary(Appointment appointment, OutputStream out) {
        throw new UnsupportedOperationException("Unimplemented method 'exportAppointmentSummary'");
    }

}
