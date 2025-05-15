package physicianconnect.presentation;

import java.util.List;

import physicianconnect.objects.Appointment;
import physicianconnect.objects.TimeSlot;

public interface AppointmentBookingView extends Viewable {
    void showAvailablePhysicians(List<Physician> physicians);
    void showAvailableTimeSlots(List<TimeSlot> timeSlots);
    TimeSlot getSelectedTimeSlot();
    void showAppointmentConfirmation(Appointment appointment);
}
