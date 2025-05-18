package logic.interfaces.ui;

import java.util.List;
import objects.MedicalHistory;
import objects.Patient;
import objects.PatientVisitSummary;

/**
 * Service interface for accessing and processing patient-related medical and
 * visit information.
 */
public interface PatientInfoService {

    /**
     * Searches through a patient's medical history using relevant keywords.
     * <p>
     * This method can be used to find entries that match specific conditions,
     * treatments, symptoms, or other medically relevant terms.
     *
     * @param patientMedicalHistory the full list of a patient's medical history
     *                              entries
     * @return a list of {@link MedicalHistory} records that match the keyword
     *         criteria
     */
    List<MedicalHistory> searchPatientHistoryByKeyword(List<MedicalHistory> patientMedicalHistory);

    /**
     * Retrieves a summary of a patient's past visits.
     * <p>
     * Each summary may include high-level visit information such as the date and
     * time,
     * physician, reason for visit, and any relevant outcomes or feedback.
     *
     * @param patient the {@link Patient} whose visit summaries are being retrieved
     * @return a list of {@link PatientVisitSummary} objects representing past
     *         visits
     */
    List<PatientVisitSummary> getPatientPastVisitSummary(Patient patient);
}
