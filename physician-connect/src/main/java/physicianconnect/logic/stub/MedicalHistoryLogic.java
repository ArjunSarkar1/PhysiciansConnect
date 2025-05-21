package logic.stub;

import objects.MedicalHistory;
import persistence.stub.MedicalHistoryStub;

import java.util.*;

public class MedicalHistoryLogic {
    
    private static final MedicalHistoryStub tempDB = MedicalHistoryStub.getInstance();

    public MedicalHistory getMedicalHistoryById(int historyId) {
        MedicalHistory result = null;
        try {
            if (historyId > 0) {
                result = tempDB.getMedicalHistory(historyId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Map<Integer, MedicalHistory> getAllMedicalHistory() {
        return tempDB.getAllMedicalHistories();
    }

    public MedicalHistory addMedicalHistory(MedicalHistory history) {
        MedicalHistory result = null;
        try {
            if (history != null) {
                result = tempDB.addMedicalHistory(history);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public MedicalHistory updateMedicalHistory(MedicalHistory history) {
        MedicalHistory result = null;
        try {
            if (history != null && history.getMedicalHistoryId() > 0) {
                result = tempDB.updateMedicalHistory(history);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public MedicalHistory deleteMedicalHistory(int historyId) {
        MedicalHistory result = null;
        try {
            if (historyId > 0) {
                result = tempDB.deleteMedicalHistory(historyId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

} 