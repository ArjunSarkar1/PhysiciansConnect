package objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import logic.AppointmentStatus;

public class Appointment {

    private Physician assignedPhysician;
    private Patient patient;
    private int officeId;
    private int durationMins;
    private String reasonForVisit;
    private String preAppointmentNotes;
    private String patientFeedback;
    private User referral;
    private List<Prescription> prescriptionList = new ArrayList<>();
    private AppointmentStatus status;

    public Appointment(Physician assignedPhysician, Patient patient, int officeId, int durationMins,
            String reasonForVisit, String preAppointmentNotes,
            String patientFeedback, User referral, AppointmentStatus status) {
        this.assignedPhysician = assignedPhysician;
        this.patient = patient;
        this.officeId = officeId;
        this.durationMins = durationMins;
        this.reasonForVisit = reasonForVisit;
        this.preAppointmentNotes = preAppointmentNotes;
        this.patientFeedback = patientFeedback;
        this.referral = referral;
        this.status = status;
    }

    // Optionally include a constructor that accepts medications
    public Appointment(Physician assignedPhysician, Patient patient, int officeId, int durationMins,
            String reasonForVisit, String preAppointmentNotes,
            String patientFeedback, User referral, List<Prescription> prescriptionLis, AppointmentStatus statust,
            AppointmentStatus status) {
        this.assignedPhysician = assignedPhysician;
        this.patient = patient;
        this.officeId = officeId;
        this.durationMins = durationMins;
        this.reasonForVisit = reasonForVisit;
        this.preAppointmentNotes = preAppointmentNotes;
        this.patientFeedback = patientFeedback;
        this.referral = referral;
        if (prescriptionList != null) {
            this.prescriptionList.addAll(prescriptionList);
        }
        this.status = status;
    }

    // Getters and Setters
    public Physician getAssignedPhysician() {
        return this.assignedPhysician;
    }

    public void setAssignedPhysician(Physician assignedPhysician) {
        this.assignedPhysician = assignedPhysician;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public int getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getDurationMins() {
        return this.durationMins;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }

    public String getReasonForVisit() {
        return this.reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getPreAppointmentNotes() {
        return this.preAppointmentNotes;
    }

    public void setPreAppointmentNotes(String preAppointmentNotes) {
        this.preAppointmentNotes = preAppointmentNotes;
    }

    public String getFeedback() {
        return this.patientFeedback;
    }

    public void setFeedback(String patientFeedback) {
        this.patientFeedback = patientFeedback;
    }

    public User getReferral() {
        return this.referral;
    }

    public void setReferral(User referral) {
        this.referral = referral;
    }

    public List<Prescription> getPrescriptionList() {
        return Collections.unmodifiableList(prescriptionList);
    }

    public void addToPrescriptionList(Prescription prescription) {
        this.prescriptionList.add(prescription);
    }

    public void removeFromPrescriptionList(Prescription prescription) {
        this.prescriptionList.remove(prescription);
    }

    public void setAppointmentStatus(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentStatus getAppointmentStatus() {
        return this.status;
    }
}
