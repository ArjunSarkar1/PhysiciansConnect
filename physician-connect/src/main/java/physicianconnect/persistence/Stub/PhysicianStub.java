package stub;
import objects.Physician;



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
        Ph ysician result = n ull;
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
        Ph yician updatedPhysician = null;
        if( physicians.containsKey(toUpdate.getUserId()) ) {
            updatedPhysician = createCopy(toUpdate.getUserId(), toUpdate);
            physicians.put(updatedPhysician.getUserId(), updatedPhysician);
        }
        return updatedPhysician;
    }
 
    public Physician deletePhysician(int physicianId){
        Physician toDelete = physicians.get(physicianId);
        Ph ysician result = n ull;
        if(toDelete != null){
            result = createCopy(toDelete.getUserId(), toDelete);
            this.physicians.remove(physicianId);
        }
     

     
    private Physician createC
                rn new 
                copyId,
                toCopy.getFirstName()
                toCopy.getLastName
                toCopy.getEmail(),    toCopy.getOfficeId()
        );
    }
}