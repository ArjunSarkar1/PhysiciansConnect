package physicianconnect.persistence.stub;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.PhysicianPersistence;

import java.util.*;

public class PhysicianPersistenceStub implements PhysicianPersistence {
    private Map<String, Physician> physicians;

    public PhysicianPersistenceStub(boolean seed) {
        physicians = new HashMap<>();
        if (seed) {
            addPhysician(new Physician("1", "Dr. Smith", "smith@hospital.com"));
            addPhysician(new Physician("2", "Dr. Lee", "lee@clinic.org"));
        }
    }

    public void addPhysician(Physician physician) {
        if (physician == null || physician.getId() == null) {
            throw new IllegalArgumentException("Physician or ID cannot be null.");
        }
        physicians.putIfAbsent(physician.getId(), physician);
    }

    @Override
    public List<Physician> getAllPhysicians() {
        return new ArrayList<>(physicians.values());
    }

    public Physician getPhysicianById(String id) {
        return physicians.get(id);
    }

    public void deletePhysicianById(String id) {
        physicians.remove(id);
    }

    public void deleteAllPhysicians() {
        physicians.clear();
    }

    public void close() {
        physicians = null;
    }
}
