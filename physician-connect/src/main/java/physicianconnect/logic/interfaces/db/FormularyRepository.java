package logic.interfaces.db;

import java.util.List;
import objects.Prescription;

/**
 * Repository interface for accessing and searching the formulary of
 * prescriptions.
 */
public interface FormularyRepository {

    /**
     * Retrieves all prescriptions available in the formulary.
     *
     * @return a list of all {@link Prescription} objects
     */
    List<Prescription> getAllPrescriptions();

    /**
     * Searches for prescriptions in the formulary that match the given keyword.
     * <p>
     * This may include matching against drug names, categories, or descriptions
     * depending on implementation.
     *
     * @param keyword the keyword to search for (e.g., "antibiotic", "ibuprofen")
     * @return a list of {@link Prescription} objects that match the search criteria
     */
    List<Prescription> searchForPrescriptionsByKeyword(String keyword);
}
