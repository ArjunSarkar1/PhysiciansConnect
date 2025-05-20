package logic.implementations.ui;

import java.util.List;

import logic.interfaces.ui.PatientInfoService;
import objects.MedicalHistory;
import objects.Patient;
import objects.PatientVisitSummary;

public class PatientInfoProvider implements PatientInfoService {

    @Override
    public List<MedicalHistory> searchPatientHistoryByKeyword(List<MedicalHistory> patientMedicalHistory) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchPatientHistoryByKeyword'");
    }

    @Override
    public List<PatientVisitSummary> getPatientPastVisitSummary(Patient patient) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPatientPastVisitSummary'");
    }

}
