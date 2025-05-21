package physicianconnect.persistence.stub;
import physicianconnect.objects.Appointment;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppointmentStub {
    private final Map<Integer, Appointment> appointments = new HashMap<>();
    private int appointmentId = 1;

    private static final AppointmentStub instance = new AppointmentStub();

    private AppointmentStub() {
    }

    public static AppointmentStub getInstance() {
        return instance;
    }

    public Appointment getAppointment(int appointmentId) {
        Appointment toReturn = this.appointments.get(appointmentId);
        Appointment result = null;
        if (toReturn != null) {
            result = createCopy(toReturn.getAppointmentId(), toReturn);
        }
        return result;
    }

    public Map<Integer, Appointment> getAllAppointment() {
        return Collections.unmodifiableMap(appointments);
    }

    public Appointment addAppointment(Appointment appointment) {
        Appointment toAdd = createCopy(this.appointmentId++, appointment);
        this.appointments.put(toAdd.getAppointmentId(), toAdd);
        return toAdd;
    }

    public Appointment updateAppointment(Appointment toUpdate) {
        Appointment updatedAppointment = null;
        if (appointments.containsKey(toUpdate.getAppointmentId())) {
            updatedAppointment = createCopy(toUpdate.getAppointmentId(), toUpdate);
            appointments.put(updatedAppointment.getAppointmentId(), updatedAppointment);
        }
        return updatedAppointment;
    }

    public Appointment deleteAppointment(int appointmentId) {
        Appointment toDelete = appointments.get(appointmentId);
        Appointment result = null;
        if (toDelete != null) {
            result = createCopy(toDelete.getAppointmentId(), toDelete);
            this.appointments.remove(appointmentId);
        }
        return result;
    }

    private Appointment createCopy(int copyId, Appointment toCopy) {
        return new Appointment(copyId,
                toCopy.getAssignedPhysician(),
                toCopy.getPatient(),
                toCopy.getOfficeId(),
                toCopy.getDurationMins(),
                toCopy.getReasonForVisit(),
                toCopy.getPreAppointmentNotes(),
                toCopy.getFeedback(),
                toCopy.getReferral(),
                toCopy.getAppointmentStatus());
    }

}
