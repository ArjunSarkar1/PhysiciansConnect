package physicianconnect.persistence.stub;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.PhysicianPersistence;

import java.util.*;

public class PhysicianPersistenceStub implements PhysicianPersistence {
    private Map<String, Physician> physicians;

    public PhysicianPersistenceStub(boolean seed) {
        physicians = new HashMap<>();
        if (seed) {
            addPhysician(new Physician("1", "Dr. Smith", "smith@hospital.com", "test123"));
            addPhysician(new Physician("2", "Dr. Lee", "lee@clinic.org", "test123"));
        }
    }

    @Override
    public void addPhysician(Physician physician) {
        if (physician == null)
            throw new IllegalArgumentException("Physician cannot be null.");

        String id = (physician.getId() == null || physician.getId().isBlank())
                ? UUID.randomUUID().toString()
                : physician.getId();

        if (!physicians.containsKey(id)) {
            physicians.put(id, new Physician(id, physician.getName(), physician.getEmail(), physician.getPassword()));
        }
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
