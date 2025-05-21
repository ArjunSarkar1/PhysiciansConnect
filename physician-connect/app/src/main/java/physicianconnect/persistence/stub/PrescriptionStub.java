<<<<<<< HEAD:physician-connect/src/main/java/physicianconnect/persistence/Stub/PrescriptionStub.java
package persistence.stub;

import objects.Prescription;
=======
package physicianconnect.persistence.stub;

import physicianconnect.objects.Prescription;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

>>>>>>> 757cf7da1123155a4a972bfd438e6ec544ce05bd:physician-connect/app/src/main/java/physicianconnect/persistence/stub/PrescriptionStub.java

public class PrescriptionStub {
    private final Map<Integer, Prescription> prescriptions = new HashMap<>();
    private int prescriptionId = 1;

    private static final PrescriptionStub instance = new PrescriptionStub();

    private PrescriptionStub() {
    }

    public static PrescriptionStub getInstance() {
        return instance;
    }

    public Prescription getPrescription(int prescriptionId) {
        Prescription toReturn = this.prescriptions.get(prescriptionId);
        Prescription result = null;
        if (toReturn != null) {
            result = createCopy(toReturn.getPrescriptionId(), toReturn);
        }
        return result;
    }

    public Map<Integer, Prescription> getAllPrescription() {
        return Collections.unmodifiableMap(prescriptions);
    }

    public Prescription addPrescription(Prescription prescription) {
        Prescription toAdd = createCopy(this.prescriptionId++, prescription);
        this.prescriptions.put(toAdd.getPrescriptionId(), toAdd);
        return toAdd;
    }

    public Prescription updatePrescription(Prescription toUpdate) {
        Prescription updatedPrescription = null;
        if (prescriptions.containsKey(toUpdate.getPrescriptionId())) {
            updatedPrescription = createCopy(toUpdate.getPrescriptionId(), toUpdate);
            prescriptions.put(updatedPrescription.getPrescriptionId(), updatedPrescription);
        }
        return updatedPrescription;
    }

    public Prescription deletePrescription(int prescriptionId) {
        Prescription toDelete = prescriptions.get(prescriptionId);
        Prescription result = null;
        if (toDelete != null) {
            result = createCopy(toDelete.getPrescriptionId(), toDelete);
            this.prescriptions.remove(prescriptionId);
        }
        return result;
    }

    private Prescription createCopy(int copyId, Prescription toCopy) {
        return new Prescription(
                copyId,
                toCopy.getName(),
                toCopy.getDosage(),
                toCopy.getFrequency(),
                toCopy.getDoctorsNote(),
                toCopy.getStartDate(),
                toCopy.getEndDate());
    }

}



