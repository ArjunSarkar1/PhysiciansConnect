package physicianconnect.presentation;

import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JComboBox;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import physicianconnect.logic.controller.PrescriptionController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.persistence.stub.MedicationPersistenceStub;
import physicianconnect.persistence.stub.PrescriptionPersistenceStub;
import physicianconnect.presentation.physician.PrescribeMedicinePanel;

class PrescribeMedicinePanelTest {

    private PrescribeMedicinePanel panel;

    @BeforeEach
    void setup() {
        // Minimal AppointmentManager stub
        AppointmentManager am = new AppointmentManager(null) {
            @Override
            public List<Appointment> getAppointmentsForPhysician(String id) {
                return List.of(new Appointment("doc1", "Patient A", LocalDateTime.now()));
            }
        };

        MedicationPersistenceStub    medStub  = new MedicationPersistenceStub(true);
        PrescriptionPersistenceStub  presStub = new PrescriptionPersistenceStub(false);
        PrescriptionController       presCtrl = new PrescriptionController(presStub);

        // Use controller-based constructor
        panel = new PrescribeMedicinePanel(am, medStub, presCtrl, "doc1", null);
    }

    @Test
    void panelInitializesWithPatientsAndMedicines() {
        JComboBox<?> patientCombo  = (JComboBox<?>) getPrivateField(panel, "patientCombo");
        JComboBox<?> medicineCombo = (JComboBox<?>) getPrivateField(panel, "medicineCombo");

        assertTrue(patientCombo.getItemCount()  > 0, "Should have at least one patient");
        assertTrue(medicineCombo.getItemCount() > 0, "Should have at least one medicine");
    }

    /*--------------------------------------------------------------*/
    private Object getPrivateField(Object obj, String name) {
        try {
            var f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
