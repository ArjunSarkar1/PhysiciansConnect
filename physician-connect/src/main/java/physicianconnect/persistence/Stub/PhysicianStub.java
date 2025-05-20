import physicianconnect.objects.Physician;

import java.util.*;

public class PhysicianStub {
    private final Map<Integer, User> physicians = new HashMap<>();
    private int physicianId = 1;

    private static final PhysicianStub instance = new PhysicianStub();

    private PhysicianStub(){}

    public static PhysicianStub getInstance(){
        return this.instance;
    }

    public Physician getPhysician(int physicianId){
        Physician toReturn = physicians.get(physicianId);
        Physician result = null;
        if(toReturn != null){
            result = new Physician(
                    toReturn.getUserId(),
                    toReturn.getFirstName(),
                    toReturn.getLastName(),
                    toReturn.getEmail(),
                    toReturn.getOfficeId()
                    // TODO: Add getter to appointments so we can return that as well
            );
        }
        return result;
    }

    public Map<Integer, User> getAllPhysicians() {
        return Collections.unmodifiableMap(physicians);
    }

    public Physician addPhysician(Physician physician){
        Physician toAdd = new Physician(
                ++this.physicianId,
                physician.getFirstName(),
                physician.getLastName(),
                physician.getEmail(),
                physician.getOfficeId()
        );
        // could use the super User const for adding Id, but going to stick with this for now
        this.physicians.put(this.physicianId, toAdd)
    }

    public Physician updatePhysician(Physician toUpdate){
        Physician updatedPhysician = null;
        if( physicians.containsKey(toUpdate.getUserId()) ) {
            updatedPhysician = new Physician(
                    toUpdate.getUserId(),
                    toUpdate.getFirstName(),
                    toUpdate.getLastName(),
                    toUpdate.getEmail(),
                    toUpdate.getOfficeId()
            );
            physicians.put(updatedPhysciain.getUserId, updatedPhysician);
        }
        return updatedPhysciain;
    }

    public Physcian deletePhyscian(int physicianId){
        Physcian toDelete = physicians.get(physicianId);
        Physcian result = null;
        if(toReturn != null){
            result = new Physcian(
                    toDelete.getUserId(),
                    toDelete.getFirstName(),
                    toDelete.getLastName(),
                    toDelete.getEmail(),
                    toDelete.getOfficeId()
            );
            this.physicians.remove(physicianId);
        }
        return result;
    }
}