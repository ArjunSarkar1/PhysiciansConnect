package physicianconnect.presentation;

import java.util.List;

import physicianconnect.objects.TimeSlot;

public interface SlotManagerView extends Viewable {
    void setExistingSlots(List<TimeSlot> slots);
    TimeSlot getNewSlot();
    void onSlotsUpdated(List<TimeSlot> updatedSlots);
}
