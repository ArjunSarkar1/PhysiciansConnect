package physicianconnect.persistence.stub;
import physicianconnect.objects.MedicalHistory;

import java.util.*;

public class MedicalHistoryStub {
    private final Map<Integer, MedicalHistory> medicalHistories  = new HashMap<>();
    private int medicalHistoryId = 1;

    private static final MedicalHistoryStub instance = new MedicalHistoryStub();

    private MedicalHistoryStub(){}

    public static MedicalHistoryStub getInstance(){
        return instance;
    }

    public MedicalHistory getMedicalHistory(int medicalHistoryId){
        MedicalHistory toReturn = this.medicalHistories.get(medicalHistoryId);
        MedicalHistory result = null;
        if(toReturn != null){
            result = createCopy(toReturn.getMedicalHistoryId(), toReturn);
        }
        return result;
    }

    public Map<Integer, MedicalHistory> getAllMedicalHistories(){
        return Collections.unmodifiableMap(this.medicalHistories);
    }

    public MedicalHistory addMedicalHistory(MedicalHistory medicalHistory){
        MedicalHistory toAdd = createCopy(this.medicalHistoryId++, medicalHistory);
        this.medicalHistories.put(toAdd.getMedicalHistoryId(), toAdd);
        return toAdd;
    }

    public MedicalHistory updateMedicalHistory(MedicalHistory toUpdate){
        MedicalHistory updatedMedicalHistory = null;
        if( medicalHistories.containsKey(toUpdate.getMedicalHistoryId())){
            updatedMedicalHistory = createCopy(toUpdate.getMedicalHistoryId(), toUpdate);
            medicalHistories.put(updatedMedicalHistory.getMedicalHistoryId(), updatedMedicalHistory);
        }
        return updatedMedicalHistory;
    }

    public MedicalHistory deleteMedicalHistory(int patientId){
        MedicalHistory toDelete = medicalHistories.get(patientId);
        MedicalHistory result = null;
        if(toDelete != null){
            result = createCopy(toDelete.getMedicalHistoryId(), toDelete);
            this.medicalHistories.remove(patientId);
        }
        return result;
    }


    private MedicalHistory createCopy(int copyId, MedicalHistory toCopy){
        return new MedicalHistory(copyId,
            toCopy.getPastConditions(),
            toCopy.getSurgeries(),
            toCopy.getAllergies(),
            toCopy.getImmunizations(),
            toCopy.getHospitalizations(),
            toCopy.getFamilyHistory()
        );
    }

}
