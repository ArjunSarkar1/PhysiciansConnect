package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * AddAppointmentDialog supports:
 *  • A 3‐arg constructor (parent, manager, physicianId)
 *  • A 4‐arg constructor (parent, manager, physicianId, onSuccessCallback)
 */
public class AddAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final String physicianId;
    private final Runnable onSuccessCallback;  // may be null

    private JTextField patientNameField;
    public JSpinner dateSpinner;
    public JSpinner timeSpinner;
    private JTextArea notesArea;

    public AddAppointmentDialog(JFrame parent,
                                AppointmentManager appointmentManager,
                                String physicianId) {
        super(parent, UIConfig.ADD_APPOINTMENT_DIALOG_TITLE, true);
        this.appointmentManager = appointmentManager;
        this.physicianId        = physicianId;
        this.onSuccessCallback  = null;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    public AddAppointmentDialog(JFrame parent,
                                AppointmentManager appointmentManager,
                                String physicianId,
                                Runnable onSuccessCallback) {
        super(parent, UIConfig.ADD_APPOINTMENT_DIALOG_TITLE, true);
        this.appointmentManager = appointmentManager;
        this.physicianId        = physicianId;
        this.onSuccessCallback  = onSuccessCallback;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        setSize(500, 500);

        // ─── Title Panel ───
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(UITheme.BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel(UIConfig.ADD_APPOINTMENT_DIALOG_TITLE);
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ─── Form Panel ───
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel nameLabel = new JLabel(UIConfig.PATIENT_NAME_LABEL);
        nameLabel.setFont(UITheme.LABEL_FONT);
        nameLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        patientNameField = new JTextField(20);
        patientNameField.setFont(UITheme.TEXTFIELD_FONT);
        formPanel.add(patientNameField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel dateLabel = new JLabel(UIConfig.DATE_LABEL);
        dateLabel.setFont(UITheme.LABEL_FONT);
        dateLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setFont(UITheme.LABEL_FONT);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        formPanel.add(dateSpinner, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel timeLabel = new JLabel(UIConfig.TIME_LABEL);
        timeLabel.setFont(UITheme.LABEL_FONT);
        timeLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(timeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setFont(UITheme.LABEL_FONT);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        formPanel.add(timeSpinner, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel notesLabel = new JLabel(UIConfig.NOTES_LABEL);
        notesLabel.setFont(UITheme.LABEL_FONT);
        notesLabel.setForeground(UITheme.TEXT_COLOR);
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 0.5;
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(UITheme.TEXTFIELD_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(
                UITheme.PRIMARY_COLOR, 1
        ));
        formPanel.add(notesScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ─── Button Panel ───
        JPanel buttonPanel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 10, 10
        ));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton saveButton   = createStyledButton(UIConfig.SAVE_BUTTON_TEXT);
        JButton cancelButton = createStyledButton(UIConfig.CANCEL_BUTTON_TEXT);

        saveButton.addActionListener(e -> saveAppointment());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveAppointment() {
        try {
            // 1) Read & validate patient name
            String patient = patientNameField.getText().trim();
            if (patient.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        UIConfig.ERROR_INVALID_NAME,
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // 2) Build a LocalDateTime from dateSpinner + timeSpinner
            Date datePart = (Date) dateSpinner.getValue();
            Date timePart = (Date) timeSpinner.getValue();
            Calendar cDate = Calendar.getInstance();
            cDate.setTime(datePart);
            Calendar cTime = Calendar.getInstance();
            cTime.setTime(timePart);

            cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
            cDate.set(Calendar.MINUTE,     cTime.get(Calendar.MINUTE));
            cDate.set(Calendar.SECOND,     0);
            cDate.set(Calendar.MILLISECOND,0);

            LocalDateTime chosenDateTime = cDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 3) Delegate to manager to create the appointment
            appointmentManager.addAppointment(
                    new Appointment(physicianId, patient, chosenDateTime)
            );

            JOptionPane.showMessageDialog(
                    this,
                    UIConfig.SUCCESS_APPOINTMENT_ADDED,
                    UIConfig.SUCCESS_DIALOG_TITLE,
                    JOptionPane.INFORMATION_MESSAGE
            );

            // 4) Invoke onSuccessCallback to reload calendars
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

            // 5) Close the dialog now that we’ve saved
            dispose();

        } catch (Exception ex) {
            // Manager will throw InvalidAppointmentException if something is invalid
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(UITheme.BACKGROUND_COLOR);

        // Determine background color
        if (text.equalsIgnoreCase(UIConfig.SAVE_BUTTON_TEXT)) {
            button.setBackground(UITheme.POSITIVE_COLOR);
        } else {
            button.setBackground(UITheme.ACCENT_LIGHT_COLOR);
        }

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        UITheme.applyHoverEffect(button);
        return button;
    }
}