package objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.AppointmentStatus;

public class Appointment {

    private Physician assignedPhysician;
    private Patient patient;
    private int officeId;
    private LocalDateTime time;
    private int durationMins;
    private String preAppointmentNotes;
    private String feedback;
    private User referral;
    private ArrayList<Prescription> prescriptionList = new ArrayList<>();
    private AppointmentStatus status;

    public Appointment(Physician assignedPhysician, Patient patient, int officeId,
            LocalDateTime time, int durationMins, String preAppointmentNotes,
            String feedback, User referral, AppointmentStatus status) {
        this.assignedPhysician = assignedPhysician;
        this.patient = patient;
        this.officeId = officeId;
        this.time = time;
        this.durationMins = durationMins;
        this.preAppointmentNotes = preAppointmentNotes;
        this.feedback = feedback;
        this.referral = referral;
        this.status = status;
    }

    // Optionally include a constructor that accepts medications
    public Appointment(Physician assignedPhysician, Patient patient, int officeId,
            LocalDateTime time, int durationMins, String preAppointmentNotes,
            String feedback, User referral, List<Prescription> prescriptionLis, AppointmentStatus statust, AppointmentStatus status) {
                    this.assignedPhysician = assignedPhysician;
                    this.patient = patient;
                    this.officeId = officeId;
                    this.time = time;
                    this.durationMins = durationMins;
                    this.preAppointmentNotes = preAppointmentNotes;
                    this.feedback = feedback;
                    this.referral = referral;
                    if (prescriptionList != null) {
                        this.prescriptionList.addAll(prescriptionList);
                    }
                    this.status = status;
    }

    // Getters and Setters
    public Physician getAssignedPhysician() {
        return assignedPhysician;
    }

    public void setAssignedPhysician(Physician assignedPhysician) {
        this.assignedPhysician = assignedPhysician;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }

    public String getPreAppointmentNotes() {
        return preAppointmentNotes;
    }

    public void setPreAppointmentNotes(String preAppointmentNotes) {
        this.preAppointmentNotes = preAppointmentNotes;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public User getReferral() {
        return referral;
    }

    public void setReferral(User referral) {
        this.referral = referral;
    }

    public List<Prescription> getPrescriptionList() {
        return Collections.unmodifiableList(prescriptionList);
    }

    public void addToMedicationList(Prescription prescription) {
        this.prescriptionList.add(prescription);
    }

    public void removeFromMedicationList(Prescription prescription) {
        this.prescriptionList.remove(prescription);
    }

    public void setAppointmentStatus(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentStatus getAppointmentStatus() {
        return this.status;
    }
}
