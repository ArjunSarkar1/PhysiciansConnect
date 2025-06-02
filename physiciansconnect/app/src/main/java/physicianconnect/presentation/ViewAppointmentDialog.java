package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ViewAppointmentDialog supports:
 *  • A 3-arg constructor (parent, manager, appointment)
 *  • A 4-arg constructor (parent, manager, appointment, onSuccessCallback)
 *  • Calls appointmentManager.deleteAppointment(appointment) instead of getId()
 */
public class ViewAppointmentDialog extends JDialog {
    private final AppointmentManager appointmentManager;
    private final Appointment appointment;
    private final Runnable onSuccessCallback;  // may be null

    private JTextArea notesArea;

    private static final Color PRIMARY_COLOR     = new Color(33, 150, 243); // Blue
    private static final Color POSITIVE_COLOR    = new Color(76, 175, 80);  // Green
    private static final Color DESTRUCTIVE_COLOR = new Color(244, 67, 54);  // Red
    private static final Color CANCEL_COLOR      = new Color(120, 124, 130);// Gray
    private static final Color BACKGROUND_COLOR  = new Color(245, 247, 250);// Light background
    private static final Color TEXT_COLOR        = new Color(34, 40, 49);   // Dark text

    private static final Font TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * 3-arg constructor (no callback).
     */
    public ViewAppointmentDialog(JFrame parent,
                                 AppointmentManager appointmentManager,
                                 Appointment appointment) {
        super(parent, "View Appointment", true);
        this.appointmentManager = appointmentManager;
        this.appointment        = appointment;
        this.onSuccessCallback  = null;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    /**
     * 4-arg constructor (with callback).
     * @param onSuccessCallback  Runnable to invoke after update/delete
     */
    public ViewAppointmentDialog(JFrame parent,
                                 AppointmentManager appointmentManager,
                                 Appointment appointment,
                                 Runnable onSuccessCallback) {
        super(parent, "View Appointment", true);
        this.appointmentManager = appointmentManager;
        this.appointment        = appointment;
        this.onSuccessCallback  = onSuccessCallback;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setSize(600, 500);

        // ─── Header Panel ───
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Patient: " + appointment.getPatientName());
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(
                "Date: " + appointment.getDateTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"))
        );
        dateLabel.setFont(LABEL_FONT);
        dateLabel.setForeground(TEXT_COLOR);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ─── Notes Panel ───
        JPanel notesPanel = new JPanel(new BorderLayout(10, 10));
        notesPanel.setBackground(BACKGROUND_COLOR);
        notesPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel notesLabel = new JLabel("Appointment Notes:");
        notesLabel.setFont(LABEL_FONT);
        notesLabel.setForeground(TEXT_COLOR);
        notesPanel.add(notesLabel, BorderLayout.NORTH);

        notesArea = new JTextArea(appointment.getNotes());
        notesArea.setFont(LABEL_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(Color.WHITE);
        notesArea.setForeground(TEXT_COLOR);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        notesPanel.add(notesScroll, BorderLayout.CENTER);

        add(notesPanel, BorderLayout.CENTER);

        // ─── Button Panel ───
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton updateButton = createStyledButton("Update Notes", POSITIVE_COLOR);
        JButton deleteButton = createStyledButton("Delete Appointment", DESTRUCTIVE_COLOR);
        JButton closeButton  = createStyledButton("Close", CANCEL_COLOR);


        // Close: simply dispose
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }
}