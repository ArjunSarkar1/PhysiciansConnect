package physicianconnect.logic.stub;

<<<<<<< HEAD:physician-connect/src/main/java/physicianconnect/logic/stub/AppointmentSlotLogic.java
import objects.AppointmentSlot;
import persistence.stub.AppointmentSlotStub;

=======
import physicianconnect.persistence.stub.*;
import physicianconnect.objects.AppointmentSlot;
>>>>>>> 757cf7da1123155a4a972bfd438e6ec544ce05bd:physician-connect/app/src/main/java/physicianconnect/logic/stub/AppointmentSlotLogic.java
import java.util.*;

public class AppointmentSlotLogic {
    private static final AppointmentSlotStub tempDB = AppointmentSlotStub.getInstance();

    public AppointmentSlot getAppointmentSlotById(int slotId) {
        AppointmentSlot result = null;
        try {
            if (slotId > 0) {
                result = tempDB.getAppointmentSlot(slotId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Map<Integer, AppointmentSlot> getAllAppointmentSlots() {
        return tempDB.getAllAppointmentSlots();
    }

    public AppointmentSlot addAppointmentSlot(AppointmentSlot slot) {
        AppointmentSlot result = null;
        try {
            if (slot != null) {
                result = tempDB.addAppointmentSlot(slot);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public AppointmentSlot updateAppointmentSlot(AppointmentSlot slot) {
        AppointmentSlot result = null;
        try {
            if (slot != null && slot.getSlotId() > 0) {
                result = tempDB.updateAppointmentSlot(slot);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public AppointmentSlot deleteAppointmentSlot(int slotId) {
        AppointmentSlot result = null;
        try {
            if (slotId > 0) {
                result = tempDB.deleteAppointmentSlot(slotId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
}