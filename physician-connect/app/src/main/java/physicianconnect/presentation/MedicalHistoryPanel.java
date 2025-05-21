package physicianconnect.presentation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;


// Changed from JFrame to JPanel, renamed class
public class MedicalHistoryPanel extends JPanel {

    // Constants - Copied from original MedicalHistoryView
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
    private static final Font FONT_TAB_INTERNAL = new Font(FONT_FAMILY, Font.BOLD, 14); // For JTabbedPane
    private static final Font FONT_TEXT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_TEXT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, 14); // Added


    private static final String ICON_SEARCH = "🔍";
    private static final String ICON_PDF = "📄";
    private static final String ICON_PRINT = "🖨️";
    
    private JComboBox<String> patientCombo;
    private JTextField searchField;
    private JButton searchButton, exportButton, printButton;
    private DefaultListModel<String> visitsModel, labsModel, notesModel;

    public MedicalHistoryPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel formCardPanel = createCard("View Patient Medical History");
        formCardPanel.add(createFormPanel(), BorderLayout.CENTER);
        add(formCardPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        patientCombo = new JComboBox<>(new String[]{"Alice Smith", "Bob Jones", "Charlie Davis"});
        styleComboBox(patientCombo);

        searchField = new JTextField();
        styleFormField(searchField);
        searchButton = new JButton(ICON_SEARCH);
        // Match button height to field for better alignment
        Dimension fldDim = searchField.getPreferredSize();
        searchButton.setPreferredSize(new Dimension(60, fldDim.height)); // Adjusted height
        stylePrimaryButton(searchButton); // Using primary style for search action

        visitsModel = new DefaultListModel<>();
        visitsModel.addElement("01 Jan 2025 – Annual Checkup");
        visitsModel.addElement("15 Feb 2025 – Follow-up Visit");

        labsModel = new DefaultListModel<>();
        labsModel.addElement("CBC: Normal");
        labsModel.addElement("Lipid Panel: Elevated LDL");

        notesModel = new DefaultListModel<>();
        notesModel.addElement("No new complaints.");
        notesModel.addElement("Continue current meds.");

        exportButton = new JButton(ICON_PDF + " Export PDF");
        styleSecondaryButton(exportButton);
        printButton = new JButton(ICON_PRINT + " Print");
        styleSecondaryButton(printButton);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_TAB_INTERNAL); // Use a specific font for internal tabs
        JList<String> visitList = new JList<>(visitsModel);
        JList<String> labList = new JList<>(labsModel);
        JList<String> noteList = new JList<>(notesModel);
        visitList.setFont(FONT_TEXT_NORMAL); // Use normal font for list items
        labList.setFont(FONT_TEXT_NORMAL);
        noteList.setFont(FONT_TEXT_NORMAL);

        tabs.addTab("Visit Summaries", new JScrollPane(visitList));
        tabs.addTab("Lab Results", new JScrollPane(labList));
        tabs.addTab("Exam Notes", new JScrollPane(noteList));

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 12, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1.0;
        c.gridx = 0;

        c.gridy = 0;
        c.gridwidth = 1;
        JPanel left = new JPanel(new BorderLayout(4, 2));
        left.setOpaque(false);
        left.add(makeLabel("Select Patient:"), BorderLayout.NORTH);
        left.add(patientCombo, BorderLayout.CENTER);
        p.add(left, c);

        c.gridx = 1;
        JPanel right = new JPanel(new BorderLayout(4, 2));
        right.setOpaque(false);
        right.add(makeLabel("Search History:"), BorderLayout.NORTH);
        JPanel searchRow = new JPanel(new BorderLayout(4, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchButton, BorderLayout.EAST);
        right.add(searchRow, BorderLayout.CENTER);
        p.add(right, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 2; // Give more weight to tabs
        p.add(tabs, c);

        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST; // Align buttons to the right
        c.weighty = 0.0;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Reduced spacing
        actions.setOpaque(false);
        actions.add(exportButton);
        actions.add(printButton);
        p.add(actions, c);
        
        c.gridy++;
        c.weighty = 0.1; // Minimal weight to push up, but not too much
        c.fill = GridBagConstraints.BOTH;
        p.add(Box.createVerticalGlue(), c);


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
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setCaretColor(COLOR_PRIMARY_ACCENT);
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_FIELD);
        cb.setForeground(COLOR_TEXT_PRIMARY);
        cb.setBackground(Color.WHITE);
        cb.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(4,6,4,6))); // Adjusted padding
        // Let layout manager decide preferred size, or set a reasonable minimum if needed
        // cb.setPreferredSize(new Dimension(250, cb.getPreferredSize().height + 4));
    }
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY_ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_ACCENT.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15))); // Adjusted padding
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
                BorderFactory.createEmptyBorder(8, 15, 8, 15))); // Adjusted padding
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
