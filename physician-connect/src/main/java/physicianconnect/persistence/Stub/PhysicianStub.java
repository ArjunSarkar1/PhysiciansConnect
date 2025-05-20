import objects.Physician;

import java.util.*;


public class PhysicianStub {
    private final Map<Integer, Physician> physicians = new HashMap<>();
    private int physicianId = 1;

    private static final PhysicianStub instance = new PhysicianStub();

    private PhysicianStub(){}

    public static PhysicianStub getInstance(){
        return instance;
    }

    public Physician getPhysician(int physicianId){
        Physician toReturn = this.physicians.get(physicianId);
        Physician result = null;
        if(toReturn != null){
            result = createCopy(toReturn.getUserId(), toReturn);
        }
        return result;
    }

    public Map<Integer, Physician> getAllPhysicians() {
        return Collections.unmodifiableMap(physicians);
    }

    public Physician addPhysician(Physician physician){
        Physician toAdd = createCopy(this.physicianId++, physician);
        this.physicians.put(toAdd.getUserId(), toAdd);
        return toAdd;
    }

    public Physician updatePhysician(Physician toUpdate){
        Physician updatedPhysician = null;
        if( physicians.containsKey(toUpdate.getUserId()) ) {
            updatedPhysician = createCopy(toUpdate.getUserId(), toUpdate);
            physicians.put(updatedPhysician.getUserId(), updatedPhysician);
        }
        return updatedPhysician;
    }

    public Physician deletePhysician(int physicianId){
        Physician toDelete = physicians.get(physicianId);
        Physician result = null;
        if(toDelete != null){
            result = createCopy(toDelete.getUserId(), toDelete);
            this.physicians.remove(physicianId);
        }
        return result;
    }


    private Physician createCopy(int copyId, Physician toCopy){
        return new Physician(
            copyId,
            toCopy.getFirstName(),
            toCopy.getLastName(),
            toCopy.getEmail(),
            toCopy.getOfficeId()
        );
    }
}