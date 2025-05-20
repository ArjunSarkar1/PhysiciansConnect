import objects.PatientVisitSummary;

import java.util.*;

public class PatientVisitSummaryStub {
    private final Map<Integer, PatientVisitSummary> visitSummaries  = new HashMap<>();
    private int visitId = 1;

    private static final PatientVisitSummaryStub instance = new PatientVisitSummaryStub();

    private PatientVisitSummaryStub(){}

    public static PatientVisitSummaryStub getInstance(){
        return instance;
    }

    public PatientVisitSummary getPatientVisitSummary(int visitId){
        PatientVisitSummary toReturn = this.visitSummaries.get(visitId);
        PatientVisitSummary result = null;
        if(toReturn != null){
            result = createCopy(toReturn.getVisitId(), toReturn);
        }
        return result;
    }

    public Map<Integer, PatientVisitSummary> getAllVisitSummaries(){
        return Collections.unmodifiableMap(this.visitSummaries);
    }

    public PatientVisitSummary addPatientVisitSummaryy(PatientVisitSummary visitSummary){
        PatientVisitSummary toAdd = createCopy(this.visitId++, visitSummary);
        this.visitSummaries.put(toAdd.getVisitId(), toAdd);
        return toAdd;
    }

    public PatientVisitSummary updatePatientVisitSummary(PatientVisitSummary toUpdate){
        PatientVisitSummary updatedVisitSummary = null;
        if( visitSummaries.containsKey(toUpdate.getVisitId())){
            updatedVisitSummary = createCopy(toUpdate.getVisitId(), toUpdate);
            visitSummaries.put(updatedVisitSummary.getVisitId(), updatedVisitSummary);
        }
        return updatedVisitSummary;
    }

    public PatientVisitSummary deletePatientVisitSummary(int visitId){
        PatientVisitSummary toDelete = visitSummaries.get(visitId);
        PatientVisitSummary result = null;
        if(toDelete != null){
            result = createCopy(toDelete.getVisitId(), toDelete);
            this.visitSummaries.remove(visitId);
        }
        return result;
    }


    private PatientVisitSummary createCopy(int copyId, PatientVisitSummary toCopy){
        return new PatientVisitSummary(copyId,
            toCopy.getVisitDateTime(),
            toCopy.getPhysicianName(),
            toCopy.getOfficeId(),
            toCopy.getDurationMins(),
            toCopy.getReasonForVisit(),
            toCopy.getPatientFeedback(),
            toCopy.getReferralName(),
            toCopy.getPrescribedMedications()
        );
    }
}
