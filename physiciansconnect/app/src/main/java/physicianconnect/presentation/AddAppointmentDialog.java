package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class AddAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final String physicianId;

    private JTextField patientNameField;
    private JTextField dateTimeField;

    public AddAppointmentDialog(JFrame parent, AppointmentManager appointmentManager, String physicianId) {
        super(parent, "Add Appointment", true);
        this.appointmentManager = appointmentManager;
        this.physicianId = physicianId;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Patient Name:");
        patientNameField = new JTextField(20);
        JLabel dateLabel = new JLabel("Date/Time (YYYY-MM-DDTHH:MM):");
        dateTimeField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(patientNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dateTimeField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveAppointment());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        pack();
    }

    private void saveAppointment() {
        try {
            String patient = patientNameField.getText().trim();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeField.getText().trim());
            appointmentManager.addAppointment(new Appointment(physicianId, patient, dateTime));
            JOptionPane.showMessageDialog(this, "Appointment added.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
