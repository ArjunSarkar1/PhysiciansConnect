package physicianconnect.presentation;

import physicianconnect.objects.Medication;
import physicianconnect.objects.Prescription;
import physicianconnect.persistence.PrescriptionPersistence;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.persistence.MedicationPersistence;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PrescribeMedicinePanel extends JPanel {
    private JComboBox<String> patientCombo;
    private JComboBox<Medication> medicineCombo;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JTextArea notesArea;
    private JButton prescribeButton;

    public PrescribeMedicinePanel(AppointmentManager appointmentManager, MedicationPersistence medicationPersistence, PrescriptionPersistence prescriptionPersistence, String physicianId, Runnable onPrescriptionAdded) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient ComboBox
        Set<String> patientNames = new TreeSet<>(appointmentManager.getAppointmentsForPhysician(physicianId)
                .stream().map(a -> a.getPatientName()).collect(Collectors.toSet()));
        patientCombo = new JComboBox<>(patientNames.toArray(new String[0]));

        // Medicine ComboBox
        List<Medication> meds = medicationPersistence.getAllMedications();
        medicineCombo = new JComboBox<>(meds.toArray(new Medication[0]));

        dosageField = new JTextField();
        frequencyField = new JTextField();
        notesArea = new JTextArea(3, 20);
        prescribeButton = new JButton("Prescribe");

        // Auto-fill dosage when medicine changes
        medicineCombo.addActionListener(e -> {
            Medication med = (Medication) medicineCombo.getSelectedItem();
            if (med != null) dosageField.setText(med.getDosage());
        });
        if (medicineCombo.getItemCount() > 0) {
            medicineCombo.setSelectedIndex(0);
            dosageField.setText(((Medication)medicineCombo.getSelectedItem()).getDosage());
        }

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Medicine:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(medicineCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Dosage:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(dosageField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(frequencyField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; add(new JScrollPane(notesArea), gbc);

        gbc.gridx = 1; gbc.gridy = 5; add(prescribeButton, gbc);

        prescribeButton.addActionListener(e -> {
            String patient = (String) patientCombo.getSelectedItem();
            Medication med = (Medication) medicineCombo.getSelectedItem();
            String dosage = dosageField.getText().trim();
            String frequency = frequencyField.getText().trim();
            String notes = notesArea.getText().trim();
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            if (patient == null || med == null || dosage.isEmpty() || frequency.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields except notes are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Prescription prescription = new Prescription(
                0, physicianId, patient, med.getName(), med.getDosage(), dosage, frequency, notes, now
            );
            prescriptionPersistence.addPrescription(prescription);
            JOptionPane.showMessageDialog(this, "Prescription added for " + patient + ": " + med.getName());
            if (onPrescriptionAdded != null) onPrescriptionAdded.run();
        });
    }
}