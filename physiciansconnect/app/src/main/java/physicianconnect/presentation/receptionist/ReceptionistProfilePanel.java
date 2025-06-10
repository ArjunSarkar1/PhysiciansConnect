package physicianconnect.presentation.receptionist;

import physicianconnect.objects.Receptionist;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.logic.manager.ReceptionistManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;

public class ReceptionistProfilePanel extends JPanel {
    private final JLabel photoLabel;
    private final JButton changePhotoButton;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JButton signOutButton;
    private final JButton editButton;
    private final JButton saveButton;
    private final JButton cancelButton;
    private final JCheckBox notifyAppointments;
    private final JCheckBox notifyBilling;
    private final JCheckBox notifyMessages;

    private final Runnable logoutCallback;
    private final Runnable onProfileUpdated;

    private final Receptionist receptionist;
    private final ReceptionistManager receptionistManager;

    private static final int MAX_PHOTO_SIZE = 200;
    private static final String PHOTO_DIR = "src/main/resources/profile_photos";

    public ReceptionistProfilePanel(Receptionist receptionist, ReceptionistManager receptionistManager,
            Runnable logoutCallback, Runnable onProfileUpdated) {
        this.receptionist = receptionist;
        this.receptionistManager = receptionistManager;
        this.logoutCallback = logoutCallback;
        this.onProfileUpdated = onProfileUpdated;

        setLayout(new BorderLayout(10, 10));

        // --- Photo Panel (left)
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        loadProfilePhoto(receptionist.getId());

        changePhotoButton = new JButton(UIConfig.CHANGE_PHOTO_BUTTON_TEXT);
        changePhotoButton.addActionListener(e -> chooseAndUploadPhoto());

        JPanel photoPanel = new JPanel(new BorderLayout(10, 10));
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(changePhotoButton, BorderLayout.SOUTH);

        // --- Form Fields (center)
        nameField = new JTextField(receptionist.getName());
        emailField = new JTextField(receptionist.getEmail());
        emailField.setEditable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel nameLabel = new JLabel(UIConfig.NAME_LABEL);
        nameLabel.setPreferredSize(new Dimension(100, 25));
        nameField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel(UIConfig.USER_EMAIL_LABEL);
        emailLabel.setPreferredSize(new Dimension(100, 25));
        emailField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        JLabel notifyLabel = new JLabel(UIConfig.NOTIFICATION_PREFS_LABEL);
        notifyAppointments = new JCheckBox(UIConfig.NOTIFY_APPOINTMENTS, receptionist.isNotifyAppointment());
        notifyBilling = new JCheckBox(UIConfig.NOTIFY_BILLING, receptionist.isNotifyBilling());
        notifyMessages = new JCheckBox(UIConfig.MESSAGES_DIALOG_TITLE, receptionist.isNotifyMessages());

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.add(notifyAppointments);
        checkboxPanel.add(notifyBilling);
        checkboxPanel.add(notifyMessages);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(notifyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(checkboxPanel, gbc);

        JPanel paddedFormPanel = new JPanel(new BorderLayout());
        paddedFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedFormPanel.add(formPanel, BorderLayout.CENTER);

        JPanel paddedPhotoPanel = new JPanel(new BorderLayout());
        paddedPhotoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        paddedPhotoPanel.add(photoPanel, BorderLayout.NORTH);
        paddedFormPanel.add(paddedPhotoPanel, BorderLayout.WEST);

        add(paddedFormPanel, BorderLayout.CENTER);

        // --- Button Panel (bottom)
        editButton = new JButton(UIConfig.EDIT_BUTTON_TEXT);
        saveButton = new JButton(UIConfig.SAVE_BUTTON_TEXT);
        cancelButton = new JButton(UIConfig.CANCEL_BUTTON_TEXT);
        signOutButton = new JButton(UIConfig.LOGIN_BUTTON_TEXT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(signOutButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        editButton.addActionListener(e -> setEditable(true));
        cancelButton.addActionListener(e -> {
            nameField.setText(receptionist.getName());
            setEditable(false);
            notifyAppointments.setSelected(receptionist.isNotifyAppointment());
            notifyBilling.setSelected(receptionist.isNotifyBilling());
            notifyMessages.setSelected(receptionist.isNotifyMessages());

        });
        saveButton.addActionListener(e -> {
            try {
                receptionistManager.validateAndUpdateReceptionist(
                        receptionist,
                        nameField.getText().trim(),
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
        });

        signOutButton.addActionListener(e -> {
            Window topWindow = SwingUtilities.getWindowAncestor(this);
            if (topWindow != null)
                topWindow.dispose();
            if (logoutCallback != null)
                logoutCallback.run();
        });

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        setEditable(false);
    }

    private void setEditable(boolean editable) {
        nameField.setEditable(editable);
        changePhotoButton.setEnabled(editable);
        saveButton.setVisible(editable);
        cancelButton.setVisible(editable);
        editButton.setVisible(!editable);
        signOutButton.setVisible(!editable);
        notifyAppointments.setEnabled(editable);
        notifyBilling.setEnabled(editable);
        notifyMessages.setEnabled(editable);
    }

    private void chooseAndUploadPhoto() {
        JFileChooser chooser = new JFileChooser(PHOTO_DIR);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || (f.getName().endsWith(".png") && f.getName().startsWith("r_"));
            }

            @Override
            public String getDescription() {
                return "Receptionist PNG Images (r_*.png)";
            }
        });

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (InputStream in = new FileInputStream(file)) {
                receptionistManager.uploadProfilePhoto(receptionist.getId(), in);
                loadProfilePhoto(receptionist.getId());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, UIConfig.PHOTO_UPLOAD_FAILED_MSG + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadProfilePhoto(String id) {
        File file = new File(PHOTO_DIR, "r_" + id + ".png");
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(img));
        } else {
            BufferedImage placeholder = new BufferedImage(MAX_PHOTO_SIZE, MAX_PHOTO_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, MAX_PHOTO_SIZE, MAX_PHOTO_SIZE);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(UIConfig.NO_PHOTO_PLACEHOLDER_TEXT, 50, 100);
            g2.dispose();
            photoLabel.setIcon(new ImageIcon(placeholder));
        }
    }
}
