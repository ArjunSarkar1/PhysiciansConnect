package logic;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import logic.stub.AppointmentSlotLogic;
import objects.AppointmentSlot;
import java.time.LocalDateTime;
import java.util.Map;

public class AppointmentSlotLogicTest {
    private AppointmentSlotLogic appointmentSlotLogic;
    private AppointmentSlot testSlot;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Before
    public void setUp() {
        appointmentSlotLogic = new AppointmentSlotLogic();

        // Create test time slots
        startTime = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        endTime = startTime.plusHours(1);

        // Create test appointment slot
        testSlot = new AppointmentSlot(
                0, // ID will be assigned by the stub
                1, // physicianId
                startTime,
                endTime,
                true, // available
                null // no appointments yet
        );
    }

    @Test
    public void testGetAppointmentSlotById_ValidId() {
        // Add a slot first
        AppointmentSlot added = appointmentSlotLogic.addAppointmentSlot(testSlot);
        assertNotNull("Added slot should not be null", added);

        // Test getting the slot
        AppointmentSlot retrieved = appointmentSlotLogic.getAppointmentSlotById(added.getSlotId());
        assertNotNull("Retrieved slot should not be null", retrieved);
        assertEquals("Slot IDs should match", added.getSlotId(), retrieved.getSlotId());
    }

    @Test
    public void testGetAppointmentSlotById_InvalidId() {
        AppointmentSlot retrieved = appointmentSlotLogic.getAppointmentSlotById(-1);
        assertNull("Retrieved slot should be null for invalid ID", retrieved);
    }

    @Test
    public void testGetAllAppointmentSlots() {
        // Add multiple slots
        AppointmentSlot slot1 = appointmentSlotLogic.addAppointmentSlot(testSlot);
        AppointmentSlot slot2 = appointmentSlotLogic.addAppointmentSlot(testSlot);

        Map<Integer, AppointmentSlot> allSlots = appointmentSlotLogic.getAllAppointmentSlots();
        assertNotNull("All slots map should not be null", allSlots);
        assertTrue("Should contain at least 2 slots", allSlots.size() >= 2);
    }

    @Test
    public void testAddAppointmentSlot_Valid() {
        AppointmentSlot added = appointmentSlotLogic.addAppointmentSlot(testSlot);
        assertNotNull("Added slot should not be null", added);
        assertTrue("Slot ID should be positive", added.getSlotId() > 0);
        assertEquals("Start time should match", startTime, added.getStartTime());
        assertEquals("End time should match", endTime, added.getEndTime());
    }

    @Test
    public void testAddAppointmentSlot_Null() {
        AppointmentSlot added = appointmentSlotLogic.addAppointmentSlot(null);
        assertNull("Added slot should be null for null input", added);
    }

    @Test
    public void testUpdateAppointmentSlot_Valid() {
        // Add a slot first
        AppointmentSlot added = appointmentSlotLogic.addAppointmentSlot(testSlot);
        assertNotNull("Added slot should not be null", added);

        // Update the slot
        added.setAvailable(false);
        AppointmentSlot updated = appointmentSlotLogic.updateAppointmentSlot(added);
        assertNotNull("Updated slot should not be null", updated);
        assertFalse("Availability should be updated", updated.isAvailable());
    }

    @Test
    public void testUpdateAppointmentSlot_Invalid() {
        AppointmentSlot updated = appointmentSlotLogic.updateAppointmentSlot(null);
        assertNull("Updated slot should be null for null input", updated);
    }

    @Test
    public void testDeleteAppointmentSlot_Valid() {
        // Add a slot first
        AppointmentSlot added = appointmentSlotLogic.addAppointmentSlot(testSlot);
        assertNotNull("Added slot should not be null", added);

        // Delete the slot
        AppointmentSlot deleted = appointmentSlotLogic.deleteAppointmentSlot(added.getSlotId());
        assertNotNull("Deleted slot should not be null", deleted);
        assertEquals("Deleted slot ID should match", added.getSlotId(), deleted.getSlotId());

        // Verify it's deleted
        AppointmentSlot retrieved = appointmentSlotLogic.getAppointmentSlotById(added.getSlotId());
        assertNull("Retrieved slot should be null after deletion", retrieved);
    }

    @Test
    public void testDeleteAppointmentSlot_Invalid() {
        AppointmentSlot deleted = appointmentSlotLogic.deleteAppointmentSlot(-1);
        assertNull("Deleted slot should be null for invalid ID", deleted);
    }
}