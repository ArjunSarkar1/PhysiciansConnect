package com.physicianconnect.presentation;

import com.physicianconnect.objects.Appointment;
import com.physicianconnect.objects.TimeSlot;
import java.util.List;

public interface AppointmentBookingView extends Viewable {
    void showAvailablePhysicians(List<Physician> physicians);
    void showAvailableTimeSlots(List<TimeSlot> timeSlots);
    TimeSlot getSelectedTimeSlot();
    void showAppointmentConfirmation(Appointment appointment);
}
