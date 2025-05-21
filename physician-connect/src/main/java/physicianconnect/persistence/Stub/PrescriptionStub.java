package stub;
import objects.Prescription;


import java.util.*;
public class PrescriptionStub { 
    private final Map<Integer, Prescription> prescriptions  = new HashMap<>();
    private int prescriptionId = 1;

    private static final PrescriptionStub instance = new PrescriptionStub();
 
    
    private PrescriptionStub(){}
 
    public static PrescriptionStub getInstance(){
        return instance;
    }
 
    public Prescription getPrescription(int prescriptionId){
        Prescription toReturn = this.prescriptions.get(prescriptionId);
        Pr escription r esult  = null;
        if(toReturn !=null){
            result = createCopy(toReturn.getPrescriptionId(), toReturn);
        }
        return result;
    }
 
    public Map<Integer, Prescription> getAllPrescription(){
        return Collections.unmodifiableMap(prescriptions);
    }
 
    public Prescription addPrescription(Prescription prescription){
        Prescription toAdd = createCopy(this.prescriptionId++, prescription);
        this.prescriptions.put(toAdd.getPrescriptionId(), toAdd);
        return toAdd;
    }
 
    public Prescription updatePrescription(Prescription toUpdate){
        Pr ecription updatedPrescription = null; 
        if( prescriptions.containsKey(toUpdate.getPrescriptionId()) ){
            updatedPrescription = createCopy(toUpdate.getPrescriptionId(), toUpdate);
            prescriptions.put(updatedPrescription.getPrescriptionId(), updatedPrescription);
        }
        return updatedPrescription;
    }
 
    public Prescription deletePrescription(int prescriptionId){
        Prescription toDelete = prescriptions.get(prescriptionId);
        Pr escription result  = null;
        if(toDelete != null){
            result = createCopy(toDelete.getPrescriptionId(), toDelete);
            this.prescriptions.remove(prescriptionId);
        }
     

     
    private Prescription createC
                rn new 
                copyId, 
                toCopy.getName(), 
                toCopy.getDosage(),
                toCopy.getFrequency(),
                toCopy.getDoctorsNote(
                toCopy.getStartDate    toCopy.getEndDate()
        );
    }

}
