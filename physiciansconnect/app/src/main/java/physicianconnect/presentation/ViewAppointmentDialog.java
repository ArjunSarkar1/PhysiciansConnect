package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ViewAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final Appointment appointment;
    private final Runnable onSuccessCallback;  // may be null

    private JTextArea notesArea;

    public ViewAppointmentDialog(JFrame parent,
                                 AppointmentManager appointmentManager,
                                 Appointment appointment) {
        super(parent, UIConfig.VIEW_APPOINTMENT_DIALOG_TITLE, true);
        this.appointmentManager = appointmentManager;
        this.appointment        = appointment;
        this.onSuccessCallback  = null;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    public ViewAppointmentDialog(JFrame parent,
                                 AppointmentManager appointmentManager,
                                 Appointment appointment,
                                 Runnable onSuccessCallback) {
        super(parent, UIConfig.VIEW_APPOINTMENT_DIALOG_TITLE, true);
        this.appointmentManager = appointmentManager;
        this.appointment        = appointment;
        this.onSuccessCallback  = onSuccessCallback;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        setSize(600, 500);

        // ─── Header Panel ───
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(UITheme.BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(
                UIConfig.PATIENT_LABEL + appointment.getPatientName()
        );
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(UIConfig.HISTORY_DATE_PATTERN);
        JLabel dateLabel = new JLabel(
                UIConfig.DATE_LABEL + appointment.getDateTime().format(dtf)
        );
        dateLabel.setFont(UITheme.LABEL_FONT);
        dateLabel.setForeground(UITheme.TEXT_COLOR);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ─── Notes Panel ───
        JPanel notesPanel = new JPanel(new BorderLayout(10, 10));
        notesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        notesPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel notesLabel = new JLabel(UIConfig.APPOINTMENT_NOTES_LABEL);
        notesLabel.setFont(UITheme.LABEL_FONT);
        notesLabel.setForeground(UITheme.TEXT_COLOR);
        notesPanel.add(notesLabel, BorderLayout.NORTH);

        notesArea = new JTextArea(appointment.getNotes());
        notesArea.setFont(UITheme.LABEL_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(UITheme.BACKGROUND_COLOR);
        notesArea.setForeground(UITheme.TEXT_COLOR);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(
                UITheme.PRIMARY_COLOR, 1
        ));
        notesPanel.add(notesScroll, BorderLayout.CENTER);

        add(notesPanel, BorderLayout.CENTER);

        // ─── Button Panel ───
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton updateButton = createStyledButton(
                UIConfig.BUTTON_UPDATE_NOTES, UITheme.SUCCESS_BUTTON_COLOR
        );
        JButton deleteButton = createStyledButton(
                UIConfig.BUTTON_DELETE_APPOINTMENT, UITheme.ERROR_BUTTON_COLOR
        );
        JButton closeButton  = createStyledButton(
                UIConfig.BUTTON_CLOSE, UITheme.CANCEL_BUTTON_COLOR
        );

        updateButton.addActionListener(e -> {
            appointment.setNotes(notesArea.getText());
            try {
                appointmentManager.updateAppointment(appointment);
                JOptionPane.showMessageDialog(
                        this,
                        UIConfig.MESSAGE_NOTES_UPDATED,
                        UIConfig.SUCCESS_DIALOG_TITLE,
                        JOptionPane.INFORMATION_MESSAGE
                );
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        UIConfig.ERROR_UPDATING_NOTES + ex.getMessage(),
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    UIConfig.CONFIRM_DELETE_MESSAGE,
                    UIConfig.CONFIRM_DIALOG_TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    appointmentManager.deleteAppointment(appointment);
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            UIConfig.ERROR_DELETING_APPOINTMENT + ex.getMessage(),
                            UIConfig.ERROR_DIALOG_TITLE,
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(UITheme.BACKGROUND_COLOR);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        UITheme.applyHoverEffect(button);
        return button;
    }
}