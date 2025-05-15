package physicianconnect.presentation;

import java.util.List;

import physicianconnect.objects.Patient;

public interface PhysicianDashboardView extends Viewable {
    void displayAssignedPatients(List<Patient> patients);
    void displaySchedule(List<Appointment> schedule);
    void displayPatientDetails(Patient patient);
    // void displayPatientHistory(List<MedicalHistory> history);
    // void displayPatientReferrals(List<Referral> referrals);
    // void displayPatientPrescriptions(List<Prescription> prescriptions);
    // void displayPatientAppointments(List<Appointment> appointments);

}
