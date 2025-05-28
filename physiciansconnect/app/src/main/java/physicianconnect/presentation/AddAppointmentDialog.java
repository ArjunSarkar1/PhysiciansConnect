package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private JTextArea notesArea;

    private static final Color PRIMARY_COLOR = new Color(33, 150, 243); // Modern blue
    private static final Color POSITIVE_COLOR = new Color(76, 175, 80); // Green
    private static final Color CANCEL_COLOR = new Color(120, 124, 130); // Gray
    private static final Color TEXT_COLOR = new Color(34, 40, 49); // Dark text
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public AddAppointmentDialog(JFrame parent, AppointmentManager appointmentManager, String physicianId) {
        super(parent, "Add Appointment", true);
        this.appointmentManager = appointmentManager;
        this.physicianId = physicianId;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setSize(500, 500);

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("New Appointment");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient Name
        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setFont(LABEL_FONT);
        nameLabel.setForeground(TEXT_COLOR);
        patientNameField = new JTextField(20);
        patientNameField.setFont(LABEL_FONT);

        // Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(LABEL_FONT);
        dateLabel.setForeground(TEXT_COLOR);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(LABEL_FONT);

        // Time
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(LABEL_FONT);
        timeLabel.setForeground(TEXT_COLOR);
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        timeSpinner.setFont(LABEL_FONT);

        // Notes
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(LABEL_FONT);
        notesLabel.setForeground(TEXT_COLOR);
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(LABEL_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));

        // Add components to form with tighter spacing
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

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(notesLabel, gbc);
        gbc.gridx = 1;
        gbc.weighty = 0.5;
        formPanel.add(notesScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> saveAppointment());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        if (text.equalsIgnoreCase("Save")) {
            button.setBackground(POSITIVE_COLOR);
        } else if (text.equalsIgnoreCase("Cancel")) {
            button.setBackground(CANCEL_COLOR);
        } else {
            button.setBackground(PRIMARY_COLOR);
        }
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.brighter());
            }
        });

        return button;
    }

    private void saveAppointment() {
        try {
            String patient = patientNameField.getText().trim();
            if (patient.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a patient name.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

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

            Appointment appointment = new Appointment(physicianId, patient, dateTime);
            appointment.setNotes(notesArea.getText());
            appointmentManager.addAppointment(appointment);
            
            JOptionPane.showMessageDialog(this, "Appointment added successfully.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}