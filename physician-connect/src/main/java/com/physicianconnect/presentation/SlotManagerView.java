package com.physicianconnect.presentation;

import com.physicianconnect.objects.TimeSlot;
import java.util.List;

public interface SlotManagerView extends Viewable {
    void setExistingSlots(List<TimeSlot> slots);
    TimeSlot getNewSlot();
    void onSlotsUpdated(List<TimeSlot> updatedSlots);
}
