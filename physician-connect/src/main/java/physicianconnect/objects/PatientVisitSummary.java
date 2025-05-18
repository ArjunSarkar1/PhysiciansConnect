package objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

final public class PatientVisitSummary {
    private LocalDateTime visitDateTime;
    private String physicianName;
    private int officeId;
    private int durationMins;
    private String reasonForVisit;
    private String patientFeedback;
    private String referralName;
    private List<Prescription> prescriptionList = new ArrayList<>();

    public PatientVisitSummary(
            LocalDateTime visitDateTime,
            String physicianName,
            int officeId,
            int durationMins,
            String reasonForVisit,
            String patientFeedback,
            String referralName,
            List<Prescription> prescriptionList) {
        this.visitDateTime = visitDateTime;
        this.physicianName = physicianName;
        this.officeId = officeId;
        this.durationMins = durationMins;
        this.reasonForVisit = reasonForVisit;
        this.patientFeedback = patientFeedback;
        this.referralName = referralName;
        if (prescriptionList != null) {
            this.prescriptionList.addAll(prescriptionList);
        }
    }

    // Getters only (this is an immutable class)
    public LocalDateTime getVisitDateTime() {
        return this.visitDateTime;
    }

    public String getPhysicianName() {
        return this.physicianName;
    }

    public int getOfficeId() {
        return this.officeId;
    }

    public int getDurationMins() {
        return this.durationMins;
    }

    public String getReasonForVisit() {
        return this.reasonForVisit;
    }

    public String getPatientFeedback() {
        return this.patientFeedback;
    }

    public String getReferralName() {
        return this.referralName;
    }

    public List<Prescription> getPrescribedMedications() {
        return this.prescriptionList;
    }
}
