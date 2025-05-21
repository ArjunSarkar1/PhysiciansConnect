package physicianconnect.presentation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute; // Not used in this version, can be removed
import java.util.Map; // Not used in this version, can be removed


// Changed from JFrame to JPanel, renamed class
public class PrescriptionPanel extends JPanel {

    // Constants - Copied from original PrescriptionView
    private static final Color COLOR_PRIMARY_ACCENT = new Color(0x1976D2);
    private static final Color COLOR_PRIMARY_ACCENT_LIGHT = new Color(0x63A4FF);
    private static final Color COLOR_BACKGROUND_COMPONENT = Color.WHITE;
    private static final Color COLOR_TEXT_PRIMARY = new Color(0x212121);
    private static final Color COLOR_BORDER_SUBTLE = new Color(0xE0E0E0);
    private static final Color COLOR_BUTTON_SECONDARY_BG = new Color(0xDEDEDE);
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x757575); // Added
    private static final Color COLOR_BUTTON_SECONDARY_HOVER_BG = new Color(0xCFCFCF); // Added


    private static final String FONT_FAMILY = Font.SANS_SERIF;
    private static final Font FONT_CARD_TITLE = new Font(FONT_FAMILY, Font.BOLD, 20);
    private static final Font FONT_TEXT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_TEXT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font(FONT_FAMILY, Font.PLAIN, 14);

    private JButton btnMedSearch, sendButton, clearButton;
    private JComboBox<String> patientCombo;
    private JTextField medSearchField, dosageField, freqField, durationField;
    private DefaultListModel<String> drugModel;
    private JList<String> drugList;
    private JTextArea drugInfoArea, notesArea;

    public PrescriptionPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel formCardPanel = createCard("Prescribe Medication");
        formCardPanel.add(createFormPanel(), BorderLayout.CENTER);
        add(formCardPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        patientCombo = new JComboBox<>(new String[]{"Alice Smith", "Bob Jones", "Charlie Davis"});
        styleComboBox(patientCombo);

        medSearchField = new JTextField();
        styleFormField(medSearchField);
        btnMedSearch = new JButton("Search Meds");
        styleSecondaryButton(btnMedSearch); // Using secondary for search to differentiate from main action

        drugModel = new DefaultListModel<>();
        drugModel.addElement("Atorvastatin 10mg");
        drugModel.addElement("Lisinopril 20mg");
        drugModel.addElement("Metformin 500mg");
        drugList = new JList<>(drugModel);
        drugList.setFont(FONT_TEXT_NORMAL);
        drugList.setVisibleRowCount(5); // Adjusted row count

        drugInfoArea = new JTextArea(5, 25); // Adjusted rows/cols
        drugInfoArea.setFont(FONT_TEXT_NORMAL);
        drugInfoArea.setEditable(false);
        drugInfoArea.setLineWrap(true); // Added for better text display
        drugInfoArea.setWrapStyleWord(true); // Added
        drugInfoArea.setText("(Select a medication to view details)\n— Usage: Once daily\n— Side effects: headache, nausea");

        dosageField = new JTextField(8); // Set preferred columns
        freqField = new JTextField(8);
        durationField = new JTextField(8);
        styleFormField(dosageField);
        styleFormField(freqField);
        styleFormField(durationField);

        notesArea = new JTextArea(4, 25); // Adjusted rows/cols
        notesArea.setFont(FONT_TEXT_NORMAL);
        notesArea.setLineWrap(true); // Added
        notesArea.setWrapStyleWord(true); // Added


        sendButton = new JButton("Send Prescription");
        clearButton = new JButton("Clear");
        stylePrimaryButton(sendButton);
        styleSecondaryButton(clearButton);

        JScrollPane listScroll = new JScrollPane(drugList);
        JScrollPane infoScroll = new JScrollPane(drugInfoArea);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 10, 6); // Adjusted insets
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST; // Align components to top-left
        c.weightx = 1.0;

        // Row 0: Patient (col 0) + Medication/Search (col 1)
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 1;
        JPanel patientPanel = new JPanel(new BorderLayout(4,2));
        patientPanel.setOpaque(false);
        patientPanel.add(makeLabel("Select Patient:"), BorderLayout.NORTH);
        patientPanel.add(patientCombo, BorderLayout.CENTER);
        p.add(patientPanel, c);

        c.gridx = 1;
        JPanel medSearchPanel = new JPanel(new BorderLayout(4,2));
        medSearchPanel.setOpaque(false);
        medSearchPanel.add(makeLabel("Medication:"), BorderLayout.NORTH);
        JPanel medRow = new JPanel(new BorderLayout(2, 0));
        medRow.setOpaque(false);
        medRow.add(medSearchField, BorderLayout.CENTER);
        medRow.add(btnMedSearch, BorderLayout.EAST);
        medSearchPanel.add(medRow, BorderLayout.CENTER);
        p.add(medSearchPanel, c);


        // Row 1: Drug list + info with labels (using JSplitPane for resizability)
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.6; // Give significant weight to this area

        JPanel listPanel = new JPanel(new BorderLayout(4, 4));
        listPanel.setOpaque(false);
        listPanel.add(makeLabel("Available Medications:"), BorderLayout.NORTH);
        listPanel.add(listScroll, BorderLayout.CENTER);
        listPanel.setMinimumSize(new Dimension(200, 150)); // Ensure it's visible

        JPanel descPanel = new JPanel(new BorderLayout(4, 4));
        descPanel.setOpaque(false);
        descPanel.add(makeLabel("Medication Details:"), BorderLayout.NORTH);
        descPanel.add(infoScroll, BorderLayout.CENTER);
        descPanel.setMinimumSize(new Dimension(200, 150));


        JSplitPane drugInfoSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, descPanel);
        drugInfoSplitPane.setOpaque(false);
        drugInfoSplitPane.setResizeWeight(0.5); // Distribute space evenly
        drugInfoSplitPane.setBorder(null);
        p.add(drugInfoSplitPane, c);


        // Row 2: DFD (full width, horizontal)
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0.0; // No vertical stretch
        JPanel dfdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Adjusted spacing
        dfdPanel.setOpaque(false);
        dfdPanel.add(makeLabel("Dosage:"));   dfdPanel.add(dosageField);
        dfdPanel.add(makeLabel("Frequency:")); dfdPanel.add(freqField);
        dfdPanel.add(makeLabel("Duration:"));  dfdPanel.add(durationField);
        p.add(dfdPanel, c);

        // Row 3: Notes label
        c.gridy++;
        c.anchor = GridBagConstraints.WEST; // Align label to left
        p.add(makeLabel("Notes / Warnings:"), c);

        // Row 4: Notes area (stretch)
        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.4; // Give some weight to notes area
        p.add(notesScroll, c);

        // Row 5: Buttons
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER; // Center buttons
        c.weighty = 0.0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);
        p.add(buttonPanel, c);
        
        // Row 6: Glue to push up if needed (though weighty on other components might suffice)
        // c.gridy++;
        // c.weighty = 0.01; // Minimal weight
        // c.fill = GridBagConstraints.VERTICAL;
        // p.add(Box.createVerticalGlue(), c);


        return p;
    }

    private JPanel createCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND_COMPONENT);
        card.setBorder(new CompoundBorder(
                new FadingBottomShadowBorder(),
                new CompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20))));
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
                BorderFactory.createEmptyBorder(6, 8, 6, 8))); // Adjusted padding
        field.setCaretColor(COLOR_PRIMARY_ACCENT);
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_FIELD);
        cb.setForeground(COLOR_TEXT_PRIMARY);
        cb.setBackground(Color.WHITE);
        cb.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(3, 5, 3, 5))); // Adjusted padding
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
        // Hover effect
        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_PRIMARY_ACCENT_LIGHT;
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(originalBg); }
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
        // Hover effect
        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_BUTTON_SECONDARY_HOVER_BG;
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(originalBg); }
        });
    }
}
