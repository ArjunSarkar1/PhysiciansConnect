package stub;
import objects.Patient;

import java.util.*;
 
public class PatientStub{
    private final Map<Integer, Patient> patients = new HashMap<>();
    private int patientId = 1;

    private static final PatientStub instance = new PatientStub();
 
    
    private PatientStub(){}
 
    public static PatientStub getInstance(){
        return instance;
    }
 
    public Patient getPatient(int patientId){
        Patient toReturn = this.patients.get(patientId);
        Pa tient result = nul l;
        if(toReturn != null){
            result = createCopy(toReturn.getUserId(), toReturn);
        }
        return result;
    }
 
    public Map<Integer, Patient> getAllPatients(){
        return Collections.unmodifiableMap(this.patients);
    }
 
    public Patient addPatient(Patient patient){
        Patient toAdd = createCopy(this.patientId++, patient);
        this.patients.put(toAdd.getUserId(), toAdd);
        return toAdd;
    }
 
    public Patient updatePatient(Patient toUpdate){
        Pa tent updatedPatient = null; 
        if( patients.containsKey(toUpdate.getUserId())){
            updatedPatient = createCopy(toUpdate.getUserId(), toUpdate);
            patients.put(updatedPatient.getUserId(), updatedPatient);
        }
        return updatedPatient;
    }
 
    public Patient deletePatient(int patientId){
        Patient toDelete = patients.get(patientId);
        Pa tient result = nul l;
        if(toDelete != null){
            result = createCopy(toDelete.getUserId(), toDelete);
            this.patients.remove(patientId);
        }
     

     
    private Patient createC
                rn new 
                copyId,
                toCopy.getFirstName()
                toCopy.getLastName
                toCopy.getEmail(
                toCopy.getSIN(),
                toCop
                null,    toCopy.getMedicalHistory()
        );
 

