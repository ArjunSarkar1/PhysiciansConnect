package physicianconnect.presentation;

import physicianconnect.AppController;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;
import physicianconnect.logic.controller.PhysicianController;
import physicianconnect.logic.controller.ReceptionistController;
import physicianconnect.logic.exceptions.InvalidCredentialException;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
        private final AppController controller;

        public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager,
                        ReceptionistManager receptionistManager, AppController controller) {
                this.controller = controller;

                setTitle(UIConfig.LOGIN_DIALOG_TITLE);
                setSize(900, 600);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                setLayout(new BorderLayout());

                // ───── Left Panel: Image ─────
                JPanel imagePanel = new JPanel(new BorderLayout());
                try {
                        Path imagePath = Paths.get("src/main/resources/picture_assets/login_image.png")
                                        .toAbsolutePath();
                        ImageIcon icon = new ImageIcon(imagePath.toString());
                        Image scaled = icon.getImage().getScaledInstance(500, 600, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaled));
                        imagePanel.add(imageLabel, BorderLayout.CENTER);

                } catch (Exception e) {
                        imagePanel.add(new JLabel("Image failed to load"), BorderLayout.CENTER);
                }

                // ───── Right Panel: Login Form ─────
                JPanel rightPanel = new JPanel(new BorderLayout());
                rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

                JPanel formPanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                JLabel emailLabel = new JLabel(UIConfig.USER_EMAIL_LABEL);
                emailLabel.setFont(UITheme.LABEL_FONT);
                emailLabel.setForeground(UITheme.TEXT_COLOR);

                JTextField emailField = new JTextField(20);
                emailField.setName("emailField");

                JLabel passLabel = new JLabel(UIConfig.USER_PASSWORD_LABEL);
                passLabel.setFont(UITheme.LABEL_FONT);
                passLabel.setForeground(UITheme.TEXT_COLOR);

                JPasswordField passField = new JPasswordField(20);
                passField.setName("passwordField");

                JLabel testInfo = new JLabel(UIConfig.TEST_LOGIN_INFO);
                testInfo.setFont(UITheme.LABEL_FONT);
                testInfo.setForeground(UITheme.TEXT_COLOR);

                JButton loginBtn = new JButton(UIConfig.LOGIN_BUTTON_TEXT);
                loginBtn.setName("loginBtn");
                loginBtn.setFont(UITheme.BUTTON_FONT);
                loginBtn.setBackground(UITheme.PRIMARY_COLOR);
                loginBtn.setForeground(UITheme.BACKGROUND_COLOR);
                loginBtn.setOpaque(true);
                loginBtn.setBorderPainted(false);
                loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                UITheme.applyHoverEffect(loginBtn);

                JButton createBtn = new JButton(UIConfig.CREAT_ACCOUNT_BUTTON_TEXT);
                createBtn.setName("createBtn");
                createBtn.setFont(UITheme.BUTTON_FONT);
                createBtn.setBackground(UITheme.SUCCESS_BUTTON_COLOR);
                createBtn.setForeground(UITheme.BACKGROUND_COLOR);
                createBtn.setOpaque(true);
                createBtn.setBorderPainted(false);
                createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                UITheme.applyHoverEffect(createBtn);

                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                formPanel.add(testInfo, gbc);

                gbc.gridy++;
                gbc.gridwidth = 1;
                gbc.anchor = GridBagConstraints.EAST;
                formPanel.add(emailLabel, gbc);

                gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                formPanel.add(emailField, gbc);

                gbc.gridx = 0;
                gbc.gridy++;
                gbc.anchor = GridBagConstraints.EAST;
                formPanel.add(passLabel, gbc);

                gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                formPanel.add(passField, gbc);

                JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
                buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
                buttons.add(loginBtn);
                buttons.add(createBtn);

                loginBtn.addActionListener(e -> {
                        String email = emailField.getText().trim();
                        String pass = new String(passField.getPassword());

                        PhysicianController physicianController = new PhysicianController(physicianManager);
                        ReceptionistController receptionistController = new ReceptionistController(receptionistManager);

                        try {
                                Physician user = physicianController.login(email, pass);
                                dispose();
                                controller.showPhysicianApp(user);
                                return;
                        } catch (InvalidCredentialException ex) {
                                try {
                                        Receptionist receptionist = receptionistController.login(email, pass);
                                        dispose();
                                        controller.showReceptionistApp(receptionist);
                                        return;
                                } catch (InvalidCredentialException rex) {
                                        JOptionPane.showMessageDialog(
                                                        this,
                                                        rex.getMessage(),
                                                        UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                }
                        }
                });

                createBtn.addActionListener(e -> {
                        String[] userTypes = { "Physician", "Receptionist" };
                        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);

                        JDialog dialog = new JDialog(this, UIConfig.CREATE_ACCOUNT_DIALOG_TITLE, true);
                        dialog.setLayout(new GridBagLayout());
                        GridBagConstraints regGbc = new GridBagConstraints();
                        regGbc.insets = new Insets(5, 5, 5, 5);
                        regGbc.fill = GridBagConstraints.HORIZONTAL;

                        JTextField nameField = new JTextField(20);
                        JTextField regEmailField = new JTextField(20);
                        JPasswordField passwordField = new JPasswordField(20);
                        JPasswordField confirmPasswordField = new JPasswordField(20);

                        regGbc.gridx = 0;
                        regGbc.gridy = 0;
                        dialog.add(new JLabel(UIConfig.ACCOUNT_TYPE_LABEL), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(userTypeCombo, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 1;
                        dialog.add(new JLabel(UIConfig.NAME_LABEL), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(nameField, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 2;
                        dialog.add(new JLabel(UIConfig.EMAIL_LABEL), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(regEmailField, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 3;
                        dialog.add(new JLabel(UIConfig.PASSWORD_LABEL), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(passwordField, regGbc);

                        regGbc.gridx = 0;
                        regGbc.gridy = 4;
                        dialog.add(new JLabel(UIConfig.CONFIRM_PASSWORD_LABEL), regGbc);
                        regGbc.gridx = 1;
                        dialog.add(confirmPasswordField, regGbc);

                        JButton registerBtn = new JButton(UIConfig.REGISTER_BUTTON_TEXT);
                        registerBtn.setBackground(new Color(76, 175, 80));
                        registerBtn.setForeground(Color.WHITE);
                        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        registerBtn.setOpaque(true);
                        registerBtn.setBorderPainted(false);
                        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        UITheme.applyHoverEffect(registerBtn);

                        regGbc.gridx = 0;
                        regGbc.gridy = 5;
                        regGbc.gridwidth = 2;
                        regGbc.anchor = GridBagConstraints.CENTER;
                        dialog.add(registerBtn, regGbc);

                        registerBtn.addActionListener(ev -> {
                                String userType = (String) userTypeCombo.getSelectedItem();
                                String name = nameField.getText().trim();
                                String email = regEmailField.getText().trim();
                                String password = new String(passwordField.getPassword());
                                String confirmPassword = new String(confirmPasswordField.getPassword());

                                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                        JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_REQUIRED_FIELD,
                                                        UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                                        JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_INVALID_EMAIL,
                                                        "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (password.length() < 6) {
                                        JOptionPane.showMessageDialog(dialog,
                                                        UIConfig.ERROR_PASSWORD_LENGTH, UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (!password.equals(confirmPassword)) {
                                        JOptionPane.showMessageDialog(dialog, UIConfig.ERROR_PASSWORD_MISMATCH,
                                                        UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                if (physicianManager.getPhysicianByEmail(email) != null ||
                                                receptionistManager.getReceptionistByEmail(email) != null) {
                                        JOptionPane.showMessageDialog(dialog,
                                                        UIConfig.ERROR_EMAIL_EXISTS, UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                        return;
                                }

                                PhysicianController physicianController = new PhysicianController(physicianManager);
                                ReceptionistController receptionistController = new ReceptionistController(
                                                receptionistManager);

                                try {
                                        if ("Physician".equals(userType)) {
                                                Physician newPhysician = physicianController.register(name, email,
                                                                password, confirmPassword);
                                                JOptionPane.showMessageDialog(dialog, UIConfig.SUCCESS_ACCOUNT_CREATED,
                                                                UIConfig.SUCCESS_DIALOG_TITLE,
                                                                JOptionPane.INFORMATION_MESSAGE);
                                                dialog.dispose();
                                                SwingUtilities.invokeLater(
                                                                () -> controller.showPhysicianApp(newPhysician));
                                        } else {
                                                Receptionist newReceptionist = receptionistController.register(name,
                                                                email, password, confirmPassword);
                                                JOptionPane.showMessageDialog(dialog, UIConfig.SUCCESS_ACCOUNT_CREATED,
                                                                UIConfig.SUCCESS_DIALOG_TITLE,
                                                                JOptionPane.INFORMATION_MESSAGE);
                                                dialog.dispose();
                                                SwingUtilities.invokeLater(
                                                                () -> controller.showReceptionistApp(newReceptionist));
                                        }
                                } catch (InvalidCredentialException ex) {
                                        JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                                                        UIConfig.ERROR_DIALOG_TITLE,
                                                        JOptionPane.ERROR_MESSAGE);
                                }
                        });

                        dialog.pack();
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                });

                // ───── Welcome Header ─────
                JPanel headerPanel = new JPanel();
                headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
                headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                headerPanel.setBackground(rightPanel.getBackground()); // Match background

                JLabel welcomeLabel = new JLabel(UIConfig.WELCOME_MESSAGE);
                welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel appLabel = new JLabel(UIConfig.APP_NAME);
                appLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
                appLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                headerPanel.add(welcomeLabel);
                headerPanel.add(Box.createRigidArea(new Dimension(0, 1)));
                headerPanel.add(appLabel);

                rightPanel.add(headerPanel, BorderLayout.NORTH);
                rightPanel.add(formPanel, BorderLayout.CENTER);
                rightPanel.add(buttons, BorderLayout.SOUTH);

                add(imagePanel, BorderLayout.WEST);
                add(rightPanel, BorderLayout.CENTER);

                setVisible(true);
        }
}
