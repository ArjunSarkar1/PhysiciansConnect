package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.objects.Prescription;
import physicianconnect.persistence.interfaces.PrescriptionPersistence;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PatientHistoryPanel extends JPanel {
    private final AppointmentManager appointmentManager;
    private final PrescriptionPersistence prescriptionPersistence;
    private final String physicianId;
    private JComboBox<String> patientCombo;
    private JTextArea historyArea;

    public PatientHistoryPanel(AppointmentManager appointmentManager, PrescriptionPersistence prescriptionPersistence, String physicianId) {
        this.appointmentManager = appointmentManager;
        this.prescriptionPersistence = prescriptionPersistence;
        this.physicianId = physicianId;
        setLayout(new BorderLayout(10, 10));

        Set<String> patientNames = appointmentManager.getAppointmentsForPhysician(physicianId)
                .stream().map(Appointment::getPatientName).collect(Collectors.toCollection(TreeSet::new));
        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));
        patientCombo.addActionListener(e -> updateHistory());

        historyArea = new JTextArea(15, 40);
        historyArea.setEditable(false);

        add(patientCombo, BorderLayout.NORTH);
        add(new JScrollPane(historyArea), BorderLayout.CENTER);

        if (!patientNames.isEmpty()) {
            patientCombo.setSelectedIndex(0);
            updateHistory();
        }
    }

    public void updateHistory() {
        String patient = (String) patientCombo.getSelectedItem();
        if (patient == null) {
            historyArea.setText("");
            return;
        }
        List<Appointment> appointments = appointmentManager.getAppointmentsForPhysician(physicianId)
                .stream().filter(a -> a.getPatientName().equals(patient)).collect(Collectors.toList());
        List<Prescription> prescriptions = prescriptionPersistence.getPrescriptionsForPatient(patient);

        StringBuilder sb = new StringBuilder();
        sb.append("Appointments:\n");
        for (Appointment a : appointments) {
            sb.append("  ").append(a.getDateTime()).append("\n");
        }
        sb.append("\nPrescriptions:\n");
        for (Prescription p : prescriptions) {
            sb.append("  ").append(p.toString()).append("\n");
        }
        historyArea.setText(sb.toString());
    }
}