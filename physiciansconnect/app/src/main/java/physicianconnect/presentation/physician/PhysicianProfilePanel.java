package physicianconnect.presentation.physician;

import physicianconnect.AppController;
import physicianconnect.logic.manager.AppointmentManager;
import physicianconnect.logic.manager.PhysicianManager;
import physicianconnect.objects.Physician;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class PhysicianProfilePanel extends JPanel {
    private final AppController appController;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField specialtyField;
    private final JTextField officeHoursField;
    private final JCheckBox notifyAppointments;
    private final JCheckBox notifyBilling;
    private final JCheckBox notifyMessages;
    private final JTextField phoneField;
    private final JTextField addressField;
    private final JButton cancelButton;
    private final JButton editButton;
    private final JButton saveButton;
    private final JButton signOutButton;
    private final JLabel photoLabel;
    private final JButton changePhotoButton;
    private final int MAX_PHOTO_SIZE = 200;
    private final Runnable onProfileUpdated;
    private final Runnable logoutCallback;
    private final Physician physician;
    private final PhysicianManager physicianManager;

    private static final String PHOTO_DIR = "profile_photos";
    private static final String[] SUPPORTED_IMAGE_TYPES = {".png", ".jpg", ".jpeg"};

    public PhysicianProfilePanel(Physician physician, PhysicianManager physicianManager,
            AppointmentManager appointmentManager,
            AppController appController, Runnable onProfileUpdated, Runnable logoutCallback) {

        this.appController = appController;
        this.physician = physician;
        this.physicianManager = physicianManager;
        this.onProfileUpdated = onProfileUpdated;
        this.logoutCallback = logoutCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create photo directory if it doesn't exist
        createPhotoDirectory();

        // Profile photo section
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadProfilePhoto(physician.getId());

        changePhotoButton = new JButton(UIConfig.CHANGE_PHOTO_BUTTON_TEXT);
        changePhotoButton.addActionListener(e -> chooseAndUploadPhoto());

        JPanel photoPanel = new JPanel(new BorderLayout(10, 10));
        photoPanel.setBackground(getBackground());
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(changePhotoButton, BorderLayout.SOUTH);

        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Initialize fields
        nameField = new JTextField(physician.getName());
        emailField = new JTextField(physician.getEmail());
        emailField.setEditable(false);
        specialtyField = new JTextField(physician.getSpecialty());
        officeHoursField = new JTextField(physician.getOfficeHours());
        phoneField = new JTextField(physician.getPhone());
        addressField = new JTextField(physician.getOfficeAddress());

        // Set field sizes
        Dimension fieldSize = new Dimension(250, 30);
        nameField.setPreferredSize(fieldSize);
        emailField.setPreferredSize(fieldSize);
        specialtyField.setPreferredSize(fieldSize);
        officeHoursField.setPreferredSize(fieldSize);
        phoneField.setPreferredSize(fieldSize);
        addressField.setPreferredSize(fieldSize);

        // Add form fields with labels
        addFormField(formPanel, gbc, UIConfig.NAME_LABEL, nameField, 0);
        addFormField(formPanel, gbc, UIConfig.USER_EMAIL_LABEL, emailField, 1);
        addFormField(formPanel, gbc, UIConfig.SPECIALTY_LABEL, specialtyField, 2);
        addFormField(formPanel, gbc, UIConfig.OFFICE_HOURS_LABEL, officeHoursField, 3);
        addFormField(formPanel, gbc, UIConfig.PHONE_LABEL, phoneField, 4);
        addFormField(formPanel, gbc, UIConfig.ADDRESS_LABEL, addressField, 5);

        // Notification preferences
        notifyAppointments = new JCheckBox(UIConfig.NOTIFY_APPOINTMENTS, physician.isNotifyAppointment());
        notifyBilling = new JCheckBox(UIConfig.NOTIFY_BILLING, physician.isNotifyBilling());
        notifyMessages = new JCheckBox(UIConfig.MESSAGES_DIALOG_TITLE, physician.isNotifyMessages());

        JLabel notifyLabel = new JLabel(UIConfig.NOTIFICATION_PREFS_LABEL);
        notifyLabel.setFont(notifyLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(notifyLabel, gbc);

        JPanel checkboxPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        checkboxPanel.setBackground(getBackground());
        checkboxPanel.add(notifyAppointments);
        checkboxPanel.add(notifyBilling);
        checkboxPanel.add(notifyMessages);
        gbc.gridx = 1;
        formPanel.add(checkboxPanel, gbc);

        // Main layout
        JPanel paddedFormPanel = new JPanel(new BorderLayout(20, 0));
        paddedFormPanel.setBackground(getBackground());
        paddedFormPanel.add(formPanel, BorderLayout.CENTER);
        paddedFormPanel.add(photoPanel, BorderLayout.WEST);
        add(paddedFormPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(getBackground());

        editButton = new JButton(UIConfig.EDIT_BUTTON_TEXT);
        saveButton = new JButton(UIConfig.SAVE_BUTTON_TEXT);
        cancelButton = new JButton(UIConfig.CANCEL_BUTTON_TEXT);
        signOutButton = new JButton(UIConfig.LOGOUT_BUTTON_TEXT);

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(signOutButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        editButton.addActionListener(e -> setEditable(true));
        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> cancelEdit());
        signOutButton.addActionListener(e -> {
            Window topWindow = SwingUtilities.getWindowAncestor(this);
            if (topWindow != null) {
                topWindow.dispose();
            }
            if (logoutCallback != null) {
                logoutCallback.run();
            }
        });

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        setEditable(false);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void createPhotoDirectory() {
        File photoDir = new File(PHOTO_DIR);
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
    }

    private void chooseAndUploadPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                for (String type : SUPPORTED_IMAGE_TYPES) {
                    if (name.endsWith(type)) return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Image Files (*.png, *.jpg, *.jpeg)";
            }
        });

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                // Validate image
                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    throw new IOException("Invalid image file");
                }

                // Resize image if needed
                if (img.getWidth() > MAX_PHOTO_SIZE || img.getHeight() > MAX_PHOTO_SIZE) {
                    img = resizeImage(img);
                }

                // Save resized image
                File outputFile = new File(PHOTO_DIR, "p_" + physician.getId() + ".png");
                ImageIO.write(img, "png", outputFile);

                // Update profile photo
                physicianManager.uploadProfilePhoto(physician.getId(), new FileInputStream(outputFile));
                loadProfilePhoto(physician.getId());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to upload photo: " + ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        double ratio = Math.min(
            (double) MAX_PHOTO_SIZE / originalImage.getWidth(),
            (double) MAX_PHOTO_SIZE / originalImage.getHeight()
        );
        
        int newWidth = (int) (originalImage.getWidth() * ratio);
        int newHeight = (int) (originalImage.getHeight() * ratio);
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        
        return resizedImage;
    }

    private void loadProfilePhoto(String physicianId) {
        File photoFile = new File(PHOTO_DIR, "p_" + physicianId + ".png");
        if (photoFile.exists()) {
            try {
                BufferedImage img = ImageIO.read(photoFile);
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaledImg));
                    return;
                }
            } catch (IOException ex) {
                // Fall through to placeholder if image loading fails
                System.out.println("Error loading photo: " + ex.getMessage());
            }
        }
        
        // Create placeholder if no photo exists or loading failed
        BufferedImage placeholder = new BufferedImage(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, MAX_PHOTO_SIZE, MAX_PHOTO_SIZE);
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String text = UIConfig.NO_PHOTO_PLACEHOLDER_TEXT;
        FontMetrics fm = g2.getFontMetrics();
        int x = (MAX_PHOTO_SIZE - fm.stringWidth(text)) / 2;
        int y = (MAX_PHOTO_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
        photoLabel.setIcon(new ImageIcon(placeholder));
    }

    private void saveProfile() {
        try {
            String name = nameField.getText().trim();
            String specialty = specialtyField.getText().trim();
            String officeHours = officeHoursField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            // Validate required fields
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (specialty.isEmpty()) {
                throw new IllegalArgumentException("Specialty cannot be empty");
            }
            if (officeHours.isEmpty()) {
                throw new IllegalArgumentException("Office hours cannot be empty");
            }
            if (phone.isEmpty()) {
                throw new IllegalArgumentException("Phone number cannot be empty");
            }
            if (address.isEmpty()) {
                throw new IllegalArgumentException("Office address cannot be empty");
            }

            physicianManager.validateAndUpdatePhysician(
                    physician,
                    name,
                    specialty,
                    officeHours,
                    phone,
                    address,
                    notifyAppointments.isSelected(),
                    notifyBilling.isSelected(),
                    notifyMessages.isSelected());

            if (onProfileUpdated != null) {
                onProfileUpdated.run();
            }

            JOptionPane.showMessageDialog(this, UIConfig.PROFILE_UPDATED_MESSAGE);
            setEditable(false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), UIConfig.VALIDATION_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelEdit() {
        nameField.setText(physician.getName());
        specialtyField.setText(physician.getSpecialty());
        officeHoursField.setText(physician.getOfficeHours());
        phoneField.setText(physician.getPhone());
        addressField.setText(physician.getOfficeAddress());
        notifyAppointments.setSelected(physician.isNotifyAppointment());
        notifyBilling.setSelected(physician.isNotifyBilling());
        notifyMessages.setSelected(physician.isNotifyMessages());
        setEditable(false);
    }

    private void setEditable(boolean editable) {
        nameField.setEditable(editable);
        specialtyField.setEditable(editable);
        officeHoursField.setEditable(editable);
        phoneField.setEditable(editable);
        addressField.setEditable(editable);
        notifyAppointments.setEnabled(editable);
        notifyBilling.setEnabled(editable);
        notifyMessages.setEnabled(editable);
        changePhotoButton.setEnabled(editable);

        saveButton.setVisible(editable);
        cancelButton.setVisible(editable);
        signOutButton.setVisible(!editable);
        editButton.setVisible(!editable);
    }
}
