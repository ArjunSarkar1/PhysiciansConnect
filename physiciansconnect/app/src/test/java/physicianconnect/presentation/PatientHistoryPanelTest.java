package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.ReferralManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Prescription;
import physicianconnect.objects.Referral;
import physicianconnect.persistence.stub.PrescriptionPersistenceStub;
import physicianconnect.persistence.stub.ReferralPersistenceStub;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientHistoryPanelTest {

    private PatientHistoryPanel panel;

    @BeforeEach
    public void setup() {
        AppointmentManager am = new AppointmentManager(null) {
            @Override
            public List<Appointment> getAppointmentsForPhysician(String id) {
                return List.of(new Appointment("doc1", "Patient A", LocalDateTime.of(2025, 6, 1, 10, 0)));
            }
        };
        PrescriptionPersistenceStub presStub = new PrescriptionPersistenceStub(false);
        presStub.addPrescription(new Prescription(0, "doc1", "Patient A", "Ibuprofen", "200mg", "200mg", "Once", "", "2025-06-01T10:00"));

        ReferralPersistenceStub referralStub = new ReferralPersistenceStub(false);
        referralStub.addReferral(new Referral(0, "doc1", "Patient A", "Lab Test", "Fasting required", "2025-06-01"));
        ReferralManager referralManager = new ReferralManager(referralStub);

        panel = new PatientHistoryPanel(am, presStub, referralManager, "doc1");
    }

    @Test
    public void testHistoryPanelShowsAppointmentsPrescriptionsAndReferrals() {
        JComboBox<?> patientCombo = (JComboBox<?>) getField(panel, "patientCombo");
        JTextArea historyArea = (JTextArea) getField(panel, "historyArea");

        patientCombo.setSelectedIndex(0);

        String text = historyArea.getText();
        assertTrue(text.contains("Appointments:"), "Should show appointments section");
        assertTrue(text.contains("Prescriptions:"), "Should show prescriptions section");
        assertTrue(text.contains("Ibuprofen"), "Should show prescription medicine");
        assertTrue(text.contains("2025-06-01T10:00"), "Should show appointment date");
        assertTrue(text.contains("Referrals:"), "Should show referrals section");
        assertTrue(text.contains("Lab Test"), "Should show referral type");
        assertTrue(text.contains("Fasting required"), "Should show referral details");
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