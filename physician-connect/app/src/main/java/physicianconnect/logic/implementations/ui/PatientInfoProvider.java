package physicianconnect.logic.implementations.ui;

import java.util.List;

import physicianconnect.logic.interfaces.ui.PatientInfoService;
import physicianconnect.objects.MedicalHistory;
import physicianconnect.objects.Patient;
import physicianconnect.objects.PatientVisitSummary;

public class PatientInfoProvider implements PatientInfoService {

    @Override
    public List<MedicalHistory> searchPatientHistoryByKeyword(List<MedicalHistory> patientMedicalHistory) {
        throw new UnsupportedOperationException("Unimplemented method 'searchPatientHistoryByKeyword'");
    }

    @Override
    public List<PatientVisitSummary> getPatientPastVisitSummary(Patient patient) {
        throw new UnsupportedOperationException("Unimplemented method 'getPatientPastVisitSummary'");
    }

}
