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
    if (physician.getId() == null || physician.getId().isBlank())
        throw new IllegalArgumentException("Physician ID cannot be null or blank.");
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

    public Physician getPhysicianByEmail(String email) {
        return physicianDB.getAllPhysicians().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public Physician login(String email, String password) {
        Physician physician = getPhysicianByEmail(email);
        if (physician != null && physician.getPassword().equals(password)) {
            return physician;
        }
        return null;
    }

}
