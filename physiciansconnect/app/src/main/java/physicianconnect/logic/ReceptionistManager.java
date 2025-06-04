package physicianconnect.logic;

import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;
import java.util.List;

public class ReceptionistManager {
    private final ReceptionistPersistence receptionistDB;

    public ReceptionistManager(ReceptionistPersistence receptionistDB) {
        this.receptionistDB = receptionistDB;
    }

    public Receptionist getReceptionistById(String id) {
        return receptionistDB.getReceptionistById(id);
    }

    public Receptionist login(String email, String password) {
        Receptionist r = getReceptionistByEmail(email);
        if (r != null && r.getPassword().equals(password)) {
            return r;
        }
        return null;
    }

    public Receptionist getReceptionistByEmail(String email) {
        return receptionistDB.getAllReceptionists().stream()
                .filter(r -> r.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<Receptionist> getAllReceptionists() {
        return receptionistDB.getAllReceptionists();
    }

    public void addReceptionist(Receptionist receptionist) {
        if (receptionist == null) {
            throw new IllegalArgumentException("Receptionist cannot be null.");
        }
        if (receptionist.getId() == null || receptionist.getId().isBlank()) {
            throw new IllegalArgumentException("Receptionist ID cannot be null or blank.");
        }
        if (receptionistDB.getReceptionistById(receptionist.getId()) == null) {
            receptionistDB.addReceptionist(receptionist);
        }
    }
}