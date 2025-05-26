package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import java.awt.*;

public class ViewAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final Appointment appointment;

    public ViewAppointmentDialog(JFrame parent, AppointmentManager appointmentManager, Appointment appointment) {
        super(parent, "View Appointment", true);
        this.appointmentManager = appointmentManager;
        this.appointment = appointment;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Patient: " + appointment.getPatientName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea details = new JTextArea("Date/Time: " + appointment.getDateTime().toString());
        details.setEditable(false);
        mainPanel.add(new JScrollPane(details), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete");
        JButton closeButton = new JButton("Close");

        deleteButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Delete this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                appointmentManager.deleteAppointment(appointment);
                dispose();
            }
        });

        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }
}
