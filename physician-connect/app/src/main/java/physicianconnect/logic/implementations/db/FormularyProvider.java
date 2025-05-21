package physicianconnect.logic.implementations.db;

import physicianconnect.logic.interfaces.db.FormularyRepository;
import java.util.List;
import physicianconnect.objects.Prescription;

public class FormularyProvider implements FormularyRepository {

    @Override
    public List<Prescription> getAllPrescriptions() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllPrescriptions'");
    }

    @Override
    public List<Prescription> searchForPrescriptionsByKeyword(String keyword) {
        throw new UnsupportedOperationException("Unimplemented method 'searchForPrescriptionsByKeyword'");
    }

}
