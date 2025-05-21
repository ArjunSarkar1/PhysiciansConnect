package physicianconnect.logic.implementations.ui;

import java.util.List;

import physicianconnect.logic.interfaces.ui.AppointmentSlotService;
import physicianconnect.objects.AppointmentSlot;
import physicianconnect.objects.Physician;

public class AppointmentSlotProvider implements AppointmentSlotService {

    @Override
    public List<AppointmentSlot> getAllAppointmentSlots(Physician physician) {
        throw new UnsupportedOperationException("Unimplemented method 'getAllAppointmentSlots'");
    }

    @Override
    public void addSlot(Physician physician, AppointmentSlot slot) {
        throw new UnsupportedOperationException("Unimplemented method 'addSlot'");
    }

    @Override
    public void removeSlot(Physician physician, int slotId) {
        throw new UnsupportedOperationException("Unimplemented method 'removeSlot'");
    }

    @Override
    public void updateSlot(Physician physician, AppointmentSlot slot) {
        throw new UnsupportedOperationException("Unimplemented method 'updateSlot'");
    }

}