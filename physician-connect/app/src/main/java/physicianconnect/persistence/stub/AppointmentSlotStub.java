<<<<<<< HEAD:physician-connect/src/main/java/physicianconnect/persistence/Stub/AppointmentSlotStub.java
package persistence.stub;

import objects.AppointmentSlot;
=======
package physicianconnect.persistence.stub;
import physicianconnect.objects.AppointmentSlot;
>>>>>>> 757cf7da1123155a4a972bfd438e6ec544ce05bd:physician-connect/app/src/main/java/physicianconnect/persistence/stub/AppointmentSlotStub.java
import java.util.*;

public class AppointmentSlotStub {
    private final Map<Integer, AppointmentSlot> appointmentSlots = new HashMap<>();
    private int appointmentSlotId = 1;

    private static final AppointmentSlotStub instance = new AppointmentSlotStub();

    private AppointmentSlotStub() {
    }

    public static AppointmentSlotStub getInstance() {
        return instance;
    }

    public AppointmentSlot getAppointmentSlot(int slotId) {
        AppointmentSlot toReturn = this.appointmentSlots.get(slotId);
        AppointmentSlot result = null;
        if (toReturn != null) {
            result = createCopy(toReturn.getSlotId(), toReturn);
        }
        return result;
    }

    public Map<Integer, AppointmentSlot> getAllAppointmentSlots() {
        return Collections.unmodifiableMap(appointmentSlots);
    }

    public AppointmentSlot addAppointmentSlot(AppointmentSlot appointment) {
        AppointmentSlot toAdd = createCopy(this.appointmentSlotId++, appointment);
        this.appointmentSlots.put(toAdd.getSlotId(), toAdd);
        return toAdd;
    }

    public AppointmentSlot updateAppointmentSlot(AppointmentSlot toUpdate) {
        AppointmentSlot updatedAppointment = null;
        if (appointmentSlots.containsKey(toUpdate.getSlotId())) {
            updatedAppointment = createCopy(toUpdate.getSlotId(), toUpdate);
            appointmentSlots.put(updatedAppointment.getSlotId(), updatedAppointment);
        }
        return updatedAppointment;
    }

    public AppointmentSlot deleteAppointmentSlot(int appointmentId) {
        AppointmentSlot toDelete = appointmentSlots.get(appointmentId);
        AppointmentSlot result = null;
        if (toDelete != null) {
            result = createCopy(toDelete.getSlotId(), toDelete);
            this.appointmentSlots.remove(appointmentId);
        }
        return result;
    }

    private AppointmentSlot createCopy(int copyId, AppointmentSlot toCopy) {
        return new AppointmentSlot(
                copyId,
                toCopy.getPhysicianId(),
                toCopy.getStartTime(),
                toCopy.getEndTime(),
                toCopy.isAvailable(),
                toCopy.getAllAppointments());
    }
}
