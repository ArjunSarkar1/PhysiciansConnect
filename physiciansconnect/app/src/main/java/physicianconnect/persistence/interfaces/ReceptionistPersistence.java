package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Receptionist;
import java.util.List;

public interface ReceptionistPersistence {
    Receptionist getReceptionistById(String id);
    Receptionist getReceptionistByEmail(String email);
    void addReceptionist(Receptionist receptionist);
    List<Receptionist> getAllReceptionists();
}