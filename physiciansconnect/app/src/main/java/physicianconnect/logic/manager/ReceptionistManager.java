package physicianconnect.logic.manager;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;
import java.util.Collections;
import java.util.List;

public class ReceptionistManager {
    private final ReceptionistPersistence receptionistDB;

    public ReceptionistManager(ReceptionistPersistence receptionistDB) {
        this.receptionistDB = receptionistDB;
    }

    public void addReceptionist(Receptionist receptionist) {
        if (receptionist == null)
            throw new IllegalArgumentException("Receptionist cannot be null.");
        if (receptionist.getId() == null || receptionist.getId().isBlank())
            throw new IllegalArgumentException("Receptionist ID cannot be null or blank.");
        if (receptionistDB.getReceptionistById(receptionist.getId()) == null) {
            receptionistDB.addReceptionist(receptionist);
        }
    }

    public List<Receptionist> getAllReceptionists() {
        return Collections.unmodifiableList(receptionistDB.getAllReceptionists());
    }

    public Receptionist getReceptionistById(String id) {
        return receptionistDB.getReceptionistById(id);
    }

    public Receptionist getReceptionistByEmail(String email) {
        return receptionistDB.getAllReceptionists().stream()
                .filter(r -> r.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public Receptionist login(String email, String password) {
        Receptionist receptionist = getReceptionistByEmail(email);
        if (receptionist != null && receptionist.getPassword().equals(password)) {
            return receptionist;
        }
        return null;
    }

}