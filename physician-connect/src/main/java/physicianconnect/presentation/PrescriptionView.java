package physicianconnect.presentation;

import java.util.List;

import physicianconnect.objects.Prescription;

public interface PrescriptionView extends Viewable {
    void setPrescriptions(List<Prescription> prescriptions);
    // void showPrescriptionDetails(Prescription prescription);   
}
