package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.persistence.stub.MedicationPersistenceStub;
import physicianconnect.persistence.stub.PrescriptionPersistenceStub;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrescribeMedicinePanelTest {

    private PrescribeMedicinePanel panel;

    @BeforeEach
    public void setup() {
        AppointmentManager am = new AppointmentManager(null) {
            @Override
            public List<Appointment> getAppointmentsForPhysician(String id) {
                return List.of(new Appointment("doc1", "Patient A", null));
            }
        };
        MedicationPersistenceStub medStub = new MedicationPersistenceStub(true);
        PrescriptionPersistenceStub presStub = new PrescriptionPersistenceStub(false);

        panel = new PrescribeMedicinePanel(am, medStub, presStub, "doc1", null);
    }

    @Test
    public void testPanelInitializesWithPatientsAndMedicines() {
        JComboBox<?> patientCombo = (JComboBox<?>) getField(panel, "patientCombo");
        JComboBox<?> medicineCombo = (JComboBox<?>) getField(panel, "medicineCombo");
        assertTrue(patientCombo.getItemCount() > 0, "Should have at least one patient");
        assertTrue(medicineCombo.getItemCount() > 0, "Should have at least one medicine");
    }

    private Object getField(Object obj, String fieldName) {
        try {
            var f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}