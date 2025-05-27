package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class AddAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final String physicianId;

    private JTextField patientNameField;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

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

        JLabel dateLabel = new JLabel("Date:");
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        JLabel timeLabel = new JLabel("Time:");
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(patientNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(timeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(timeSpinner, gbc);

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

            Date date = (Date) dateSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            LocalDateTime dateTime = cal.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            appointmentManager.addAppointment(new Appointment(physicianId, patient, dateTime));
            JOptionPane.showMessageDialog(this, "Appointment added.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}