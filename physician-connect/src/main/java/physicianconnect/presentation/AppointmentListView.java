package physicianconnect.presentation;


import java.util.List;

import physicianconnect.objects.Appointment;

//managge upcoming appointments
public interface AppointmentListView extends Viewable {
    void setAppointments(List<Appointment> appointments);
    // void showUpcomingAppointments(List<Appointment> appointments);
    // void showAppointmentDetails(Appointment appointment);
}
