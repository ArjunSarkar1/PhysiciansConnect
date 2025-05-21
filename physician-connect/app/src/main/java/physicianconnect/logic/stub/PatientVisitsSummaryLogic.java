package physicianconnect.logic.stub;

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import physicianconnect.objects.PatientVisitSummary;
import physicianconnect.persistence.stub.PatientVisitSummaryStub;

public class PatientVisitsSummaryLogic {
    private static final Logger LOGGER = Logger.getLogger(PatientVisitsSummaryLogic.class.getName());
    private static final PatientVisitSummaryStub visitSummaryDB = PatientVisitSummaryStub.getInstance();

    public PatientVisitSummary getVisitSummaryById(int summaryId) {
        PatientVisitSummary result = null;
        try {
            if (summaryId > 0) {
                result = visitSummaryDB.getPatientVisitSummary(summaryId);
            } else {
                LOGGER.warning("Invalid summary ID provided: " + summaryId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving visit summary: " + e.getMessage(), e);
        }
        return result;
    }

    public Map<Integer, PatientVisitSummary> getAllVisitSummaries() {
        try {
            return visitSummaryDB.getAllVisitSummaries();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all visit summaries: " + e.getMessage(), e);
            return Map.of(); // Return empty map instead of null
        }
    }

    public PatientVisitSummary addVisitSummary(PatientVisitSummary summary) {
        PatientVisitSummary result = null;
        try {
            if (summary != null && isValidVisitSummary(summary)) {
                result = visitSummaryDB.addPatientVisitSummary(summary);
            } else {
                LOGGER.warning("Invalid visit summary provided for addition");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding visit summary: " + e.getMessage(), e);
        }
        return result;
    }

    public PatientVisitSummary updateVisitSummary(PatientVisitSummary summary) {
        PatientVisitSummary result = null;
        try {
            if (summary != null && summary.getVisitId() > 0 && isValidVisitSummary(summary)) {
                result = visitSummaryDB.updatePatientVisitSummary(summary);
            } else {
                LOGGER.warning("Invalid visit summary provided for update");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating visit summary: " + e.getMessage(), e);
        }
        return result;
    }

    public PatientVisitSummary deleteVisitSummary(int summaryId) {
        PatientVisitSummary result = null;
        try {
            if (summaryId > 0) {
                result = visitSummaryDB.deletePatientVisitSummary(summaryId);
            } else {
                LOGGER.warning("Invalid summary ID provided for deletion: " + summaryId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting visit summary: " + e.getMessage(), e);
        }
        return result;
    }

    private boolean isValidVisitSummary(PatientVisitSummary summary) {
        if (summary == null) return false;
        
        // Validate required fields
        if (summary.getVisitDateTime() == null) {
            LOGGER.warning("Visit date/time cannot be null");
            return false;
        }
        
        if (summary.getPhysicianName() == null || summary.getPhysicianName().trim().isEmpty()) {
            LOGGER.warning("Physician name cannot be empty");
            return false;
        }
        
        if (summary.getOfficeId() <= 0) {
            LOGGER.warning("Invalid office ID: " + summary.getOfficeId());
            return false;
        }
        
        if (summary.getDurationMins() <= 0) {
            LOGGER.warning("Invalid duration: " + summary.getDurationMins());
            return false;
        }
        
        if (summary.getReasonForVisit() == null || summary.getReasonForVisit().trim().isEmpty()) {
            LOGGER.warning("Reason for visit cannot be empty");
            return false;
        }
        
        return true;
    }
}
