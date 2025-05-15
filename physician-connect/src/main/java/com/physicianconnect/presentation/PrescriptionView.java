package com.physicianconnect.presentation;

import com.physicianconnect.objects.Prescription;
import java.util.List;

public interface PrescriptionView extends Viewable {
    void setPrescriptions(List<Prescription> prescriptions);
    // void showPrescriptionDetails(Prescription prescription);   
}
