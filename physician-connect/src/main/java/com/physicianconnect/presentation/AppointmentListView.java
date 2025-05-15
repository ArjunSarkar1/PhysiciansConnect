package com.physicianconnect.presentation;


import com.physicianconnect.objects.Appointment;
import java.util.List;

//managge upcoming appointments
public interface AppointmentListView extends Viewable {
    void setAppointments(List<Appointment> appointments);
    // void showUpcomingAppointments(List<Appointment> appointments);
    // void showAppointmentDetails(Appointment appointment);
}
