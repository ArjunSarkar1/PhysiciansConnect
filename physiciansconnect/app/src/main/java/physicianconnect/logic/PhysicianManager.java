package physicianconnect.logic;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.PhysicianPersistence;

import java.util.Collections;
import java.util.List;

public class PhysicianManager {

    private final PhysicianPersistence physicianDB;

    public PhysicianManager(PhysicianPersistence physicianDB) {
        this.physicianDB = physicianDB;
    }

    public void addPhysician(Physician physician) {
        if (physician == null)
            throw new IllegalArgumentException("Physician cannot be null.");
        if (physicianDB.getPhysicianById(physician.getId()) == null) {
            physicianDB.addPhysician(physician);
        }
    }

    public void removePhysician(String id) {
        physicianDB.deletePhysicianById(id);
    }

    public List<Physician> getAllPhysicians() {
        return Collections.unmodifiableList(physicianDB.getAllPhysicians());
    }

    public Physician getPhysicianById(String id) {
        return physicianDB.getPhysicianById(id);
    }

    public void deleteAll() {
        physicianDB.deleteAllPhysicians();
    }
}
