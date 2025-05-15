package com.physicianconnect.presentation;

import com.physicianconnect.objects.Patient;
import java.util.List;

public interface PhysicianDashboardView extends Viewable {
    void displayAssignedPatients(List<Patient> patients);
    void displaySchedule(List<Appointment> schedule);
    void displayPatientDetails(Patient patient);
    // void displayPatientHistory(List<MedicalHistory> history);
    // void displayPatientReferrals(List<Referral> referrals);
    // void displayPatientPrescriptions(List<Prescription> prescriptions);
    // void displayPatientAppointments(List<Appointment> appointments);

}
