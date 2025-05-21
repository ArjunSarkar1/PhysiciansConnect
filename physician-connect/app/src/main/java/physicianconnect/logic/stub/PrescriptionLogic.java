package physicianconnect.logic.stub;

import physicianconnect.persistence.stub.*;
import physicianconnect.objects.Prescription;
import java.util.*;

public class PrescriptionLogic {
    private static final PrescriptionStub tempDB = PrescriptionStub.getInstance();

    public Prescription getPrescriptionById(int prescriptionId) {
        Prescription result = null;
        try {
            if (prescriptionId > 0) {
                result = tempDB.getPrescription(prescriptionId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Map<Integer, Prescription> getAllPrescriptions() {
        return tempDB.getAllPrescription();
    }

    public Prescription addPrescription(Prescription prescription) {
        Prescription result = null;
        try {
            if (prescription != null) {
                result = tempDB.addPrescription(prescription);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Prescription updatePrescription(Prescription prescription) {
        Prescription result = null;
        try {
            if (prescription != null && prescription.getPrescriptionId() > 0) {
                result = tempDB.updatePrescription(prescription);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Prescription deletePrescription(int prescriptionId) {
        Prescription result = null;
        try {
            if (prescriptionId > 0) {
                result = tempDB.deletePrescription(prescriptionId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
} 