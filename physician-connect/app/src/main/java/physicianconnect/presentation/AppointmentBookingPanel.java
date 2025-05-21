package physicianconnect.presentation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

// Changed from JFrame to JPanel, renamed class
public class AppointmentBookingPanel extends JPanel {

    // Constants - Copied from original AppointmentBookingView, ensure they don't conflict
    // or consider a shared constants class.
    private static final Color COLOR_PRIMARY_ACCENT = new Color(0x1976D2);
    private static final Color COLOR_PRIMARY_ACCENT_LIGHT = new Color(0x63A4FF);
    private static final Color COLOR_BACKGROUND_COMPONENT = Color.WHITE;
    private static final Color COLOR_TEXT_PRIMARY = new Color(0x212121);
    private static final Color COLOR_BORDER_SUBTLE = new Color(0xE0E0E0);
    private static final Color COLOR_BUTTON_SECONDARY_BG = new Color(0xDEDEDE);
    private static final Color COLOR_BUTTON_SECONDARY_HOVER_BG = new Color(0xCFCFCF);
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x757575);


    private static final String FONT_FAMILY = Font.SANS_SERIF;
    private static final Font FONT_CARD_TITLE = new Font(FONT_FAMILY, Font.BOLD, 20);
    private static final Font FONT_TEXT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_TEXT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font(FONT_FAMILY, Font.PLAIN, 14);

    private static final String ICON_CREATE = "✔️"; 
    private static final String ICON_DISCARD = "❌"; 

    private JTextField patientField;
    private JTextField reasonField;
    private JSpinner dateSpinner;
    private JList<String> slotList;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JButton createAppointmentButton;
    private JButton discardButton;

    public AppointmentBookingPanel() {
        setLayout(new BorderLayout()); 
        setOpaque(false); 
        initComponents();
    }

    private void initComponents() {
        JPanel formCardPanel = createCard("Book / Edit Appointment"); 
        formCardPanel.add(createFormPanel(), BorderLayout.CENTER); 

        // This panel is added to PhysicianDashboardView's CardLayout,
        // so it should fill the space given to it.
        // Padding is handled by PhysicianDashboardView's contentWrapper.
        add(formCardPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); 
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8); 
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST; 

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1; 
        panel.add(makeLabel("Patient Name:"), c);
        c.gridx = 1;
        c.weightx = 0.9; 
        patientField = new JTextField(25); 
        styleFormField(patientField);
        panel.add(patientField, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(makeLabel("Reason for Visit:"), c);
        c.gridx = 1;
        reasonField = new JTextField(25);
        styleFormField(reasonField);
        panel.add(reasonField, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(makeLabel("Appointment Date:"), c);
        c.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd MMM yy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(FONT_FIELD);
        panel.add(dateSpinner, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        panel.add(makeLabel("Available Time Slots (suggested):"), c);
        c.gridy = 4;
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("09:00 – 10:00");
        listModel.addElement("10:30 – 11:30");
        listModel.addElement("14:00 – 15:00");
        listModel.addElement("15:30 - 16:30");
        slotList = new JList<>(listModel);
        slotList.setFont(FONT_TEXT_NORMAL);
        slotList.setVisibleRowCount(4);
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(slotList);
        scrollPane.setPreferredSize(new Dimension(300, 80)); 
        panel.add(scrollPane, c);

        c.gridy = 5;
        c.gridwidth = 1;
        c.gridx = 0;
        panel.add(makeLabel("Or Select Custom Time:"), c);
        c.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setOpaque(false);

        SpinnerDateModel fromTimeModel = new SpinnerDateModel();
        fromSpinner = new JSpinner(fromTimeModel);
        fromSpinner.setEditor(new JSpinner.DateEditor(fromSpinner, "HH:mm"));
        fromSpinner.setFont(FONT_FIELD);

        SpinnerDateModel toTimeModel = new SpinnerDateModel();
        toSpinner = new JSpinner(toTimeModel);
        toSpinner.setEditor(new JSpinner.DateEditor(toSpinner, "HH:mm"));
        toSpinner.setFont(FONT_FIELD);

        timePanel.add(new JLabel("From:"));
        timePanel.add(fromSpinner);
        timePanel.add(Box.createHorizontalStrut(10));
        timePanel.add(new JLabel("To:"));
        timePanel.add(toSpinner);
        panel.add(timePanel, c);

        c.gridy = 6;
        c.gridx = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER; 
        c.fill = GridBagConstraints.NONE; 
        c.insets = new Insets(20, 10, 10, 10); 

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        createAppointmentButton = new JButton(ICON_CREATE + " Create Appointment");
        stylePrimaryButton(createAppointmentButton);
        buttonPanel.add(createAppointmentButton);

        discardButton = new JButton(ICON_DISCARD + " Discard Changes");
        styleSecondaryButton(discardButton); 
        buttonPanel.add(discardButton);

        panel.add(buttonPanel, c);

        c.gridy = 7;
        c.weighty = 1.0; // Pushes content up
        c.fill = GridBagConstraints.BOTH;
        panel.add(Box.createGlue(), c);

        return panel;
    }

    private JPanel createCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND_COMPONENT);
        card.setBorder(new CompoundBorder(
                new FadingBottomShadowBorder(),
                new CompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20))));
        // Let the layout manager of PhysicianDashboardView determine the size.
        // card.setPreferredSize(new Dimension(PhysicianDashboardView.FRAME_W * 2 / 3, PhysicianDashboardView.FRAME_H * 2 / 3));

        JLabel lblCardTitle = new JLabel(titleText);
        lblCardTitle.setFont(FONT_CARD_TITLE);
        lblCardTitle.setForeground(COLOR_PRIMARY_ACCENT.darker());
        lblCardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); 

        card.add(lblCardTitle, BorderLayout.NORTH);
        return card;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT_BOLD); 
        lbl.setForeground(COLOR_TEXT_PRIMARY);
        return lbl;
    }

    private void styleFormField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setForeground(COLOR_TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10) 
        ));
        field.setCaretColor(COLOR_PRIMARY_ACCENT); 
    }
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY_ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_ACCENT.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_PRIMARY_ACCENT_LIGHT;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_BUTTON_SECONDARY_BG); 
        button.setForeground(COLOR_TEXT_PRIMARY); 
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_TEXT_SECONDARY, 1), 
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_BUTTON_SECONDARY_HOVER_BG; 
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
    }
}
