package logic.stub;

import objects.Appointment;
import persistence.stub.AppointmentStub;

import java.util.*;

public class AppointmentLogic {
    private static final AppointmentStub tempDB = AppointmentStub.getInstance();

    public Appointment getAppointmentById(int appointmentId) {
        Appointment result = null;
        try {
            if (appointmentId > 0) {
                result = tempDB.getAppointment(appointmentId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Map<Integer, Appointment> getAllAppointments() {
        return tempDB.getAllAppointment();
    }

    public Appointment addAppointment(Appointment appointment) {
        Appointment result = null;
        try {
            if (appointment != null) {
                result = tempDB.addAppointment(appointment);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Appointment updateAppointment(Appointment appointment) {
        Appointment result = null;
        try {
            if (appointment != null && appointment.getAppointmentId() > 0) {
                result = tempDB.updateAppointment(appointment);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Appointment deleteAppointment(int appointmentId) {
        Appointment result = null;
        try {
            if (appointmentId > 0) {
                result = tempDB.deleteAppointment(appointmentId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
} 