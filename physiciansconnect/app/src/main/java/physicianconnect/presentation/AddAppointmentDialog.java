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

/**
 * AddAppointmentDialog supports:
 *  • A 3-arg constructor (parent, manager, physicianId)
 *  • A 4-arg constructor (parent, manager, physicianId, onSuccessCallback)
 */
public class AddAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final String physicianId;
    private final Runnable onSuccessCallback;  // may be null

    private JTextField patientNameField;
    public JSpinner dateSpinner;
    public JSpinner timeSpinner;
    private JTextArea notesArea;

    private static final Color PRIMARY_COLOR    = new Color(33, 150, 243); // Blue
    private static final Color POSITIVE_COLOR   = new Color(76, 175, 80);  // Green
    private static final Color CANCEL_COLOR     = new Color(120, 124, 130);// Gray
    private static final Color TEXT_COLOR       = new Color(34, 40, 49);   // Dark text
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);// Light blue background

    private static final Font TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * 3-arg constructor (no callback).
     */
    public AddAppointmentDialog(JFrame parent,
                                AppointmentManager appointmentManager,
                                String physicianId) {
        super(parent, "Add Appointment", true);
        this.appointmentManager = appointmentManager;
        this.physicianId        = physicianId;
        this.onSuccessCallback  = null;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    /**
     * 4-arg constructor (with callback).
     * @param onSuccessCallback  Runnable to invoke after successful save
     */
    public AddAppointmentDialog(JFrame parent,
                                AppointmentManager appointmentManager,
                                String physicianId,
                                Runnable onSuccessCallback) {
        super(parent, "Add Appointment", true);
        this.appointmentManager = appointmentManager;
        this.physicianId        = physicianId;
        this.onSuccessCallback  = onSuccessCallback;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    /**
     * Builds the UI and wires up the Save/Cancel buttons.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setSize(500, 500);

        // ─── Title Panel ───
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("New Appointment");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ─── Form Panel ───
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Patient Name:*");
        nameLabel.setFont(LABEL_FONT);
        nameLabel.setForeground(TEXT_COLOR);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        patientNameField = new JTextField(20);
        patientNameField.setFont(LABEL_FONT);
        formPanel.add(patientNameField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(LABEL_FONT);
        dateLabel.setForeground(TEXT_COLOR);
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setFont(LABEL_FONT);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        formPanel.add(dateSpinner, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(LABEL_FONT);
        timeLabel.setForeground(TEXT_COLOR);
        formPanel.add(timeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setFont(LABEL_FONT);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        formPanel.add(timeSpinner, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(LABEL_FONT);
        notesLabel.setForeground(TEXT_COLOR);
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 0.5;
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(LABEL_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        formPanel.add(notesScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ─── Button Panel ───
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveButton   = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> saveAppointment());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Called when the user clicks “Save.”
     * Performs validation, conflict check, calls AppointmentManager, then callback.
     */
    private void saveAppointment() {
        try {
            // 1) Read & validate patient name
            String patient = patientNameField.getText().trim();
            if (patient.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a patient name.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) Build a LocalDateTime from dateSpinner + timeSpinner
            Date datePart = (Date) dateSpinner.getValue();
            Date timePart = (Date) timeSpinner.getValue();
            Calendar cDate = Calendar.getInstance();
            cDate.setTime(datePart);
            Calendar cTime = Calendar.getInstance();
            cTime.setTime(timePart);

            // Overwrite hours/minutes on the date
            cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
            cDate.set(Calendar.MINUTE,     cTime.get(Calendar.MINUTE));
            cDate.set(Calendar.SECOND,     0);
            cDate.set(Calendar.MILLISECOND,0);

            LocalDateTime chosenDateTime =
                    cDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

            // 3) Check for conflict before creating a new Appointment
            //    Use your AppointmentManager.isSlotAvailable(...)
            //    ‒ It returns true if no appointment exists for this physician at exactly chosenDateTime
            if (!appointmentManager.isSlotAvailable(physicianId, chosenDateTime)) {
                // If false, that slot is already taken
                JOptionPane.showMessageDialog(this,
                        "That time slot is already booked.\nPlease pick a different time.",
                        "Time Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4) Construct a brand‐new Appointment using the correct constructor
            //    (physicianId, patientName, LocalDateTime)
            Appointment newAppt = new Appointment(physicianId, patient, chosenDateTime);
            newAppt.setNotes(notesArea.getText());
            appointmentManager.addAppointment(newAppt);

            JOptionPane.showMessageDialog(this,
                    "Appointment added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // 5) Invoke onSuccessCallback to reload calendars
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

            // 6) Close the dialog now that we’ve saved
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        if (text.equalsIgnoreCase("Save")) {
            button.setBackground(POSITIVE_COLOR);
        } else { // “Cancel”
            button.setBackground(CANCEL_COLOR);
        }
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }
        });

        return button;
    }
}