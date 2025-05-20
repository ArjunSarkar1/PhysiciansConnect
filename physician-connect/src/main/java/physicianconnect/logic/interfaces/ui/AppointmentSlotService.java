package logic.interfaces.ui;

import java.util.List;
import objects.AppointmentSlot;
import objects.Physician;

/**
 * Service interface for managing appointment slots of physicians.
 */
public interface AppointmentSlotService {

    /**
     * Retrieves all available appointment slots for the specified physician.
     * <p>
     * For example, if the physician is available on Monday from 11:00 AM to 4:00
     * PM,
     * this method would return a list containing one or more
     * {@link AppointmentSlot}
     * objects representing those recurring availability blocks.
     *
     * @param physician the physician whose appointment slots are being retrieved
     * @return a list of available {@link AppointmentSlot} objects
     */
    List<AppointmentSlot> getAllAppointmentSlots(Physician physician);

    /**
     * Adds a new appointment slot for the specified physician.
     *
     * @param physician the physician to whom the slot will be assigned
     * @param slot      the {@link AppointmentSlot} to be added
     */
    void addSlot(Physician physician, AppointmentSlot slot);

    /**
     * Removes an existing appointment slot for the specified physician.
     *
     * @param physician the physician whose slot is being removed
     * @param slotId    the unique identifier of the slot to be removed
     */
    void removeSlot(Physician physician, int slotId);

    /**
     * Updates the details of an existing appointment slot for the specified
     * physician and notifies the patients. In a real application, this would
     * typically be implemented using an
     * external API or event system. For this simplified version, the implementation
     * will output changes to the console.
     *
     * @param physician the physician whose slot is being updated
     * @param slot      the updated {@link AppointmentSlot} object
     */
    void updateSlot(Physician physician, AppointmentSlot slot);
}