package physicianconnect.presentation;

import physicianconnect.AppController;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.logic.ReceptionistManager;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private final AppController controller;

    public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager,
                       ReceptionistManager receptionistManager, AppController controller) {
        this.controller = controller;

        setTitle("PhysicianConnect Login");
        setSize(420, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel testInfo = new JLabel("Test login / pass: test@email.com / test123");
        testInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        testInfo.setForeground(new Color(0, 0, 0));

        JLabel emailLabel = new JLabel("example@email.com");
        JTextField emailField = new JTextField(20);
        emailField.setName("emailField");

        JLabel passLabel = new JLabel("password");
        JPasswordField passField = new JPasswordField(20);
        passField.setName("passwordField");

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");
        loginBtn.setName("loginBtn");
        createBtn.setName("createBtn");

        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(loginBtn);

        createBtn.setBackground(new Color(76, 175, 80));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createBtn.setOpaque(true);
        createBtn.setBorderPainted(false);
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(createBtn);

        // Add components to panel with GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(testInfo, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        panel.add(passLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(passField, gbc);

        // Buttons row
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttons.add(loginBtn);
        buttons.add(createBtn);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttons, gbc);

        add(panel, BorderLayout.CENTER);

        // Login logic (auto-detect user type)
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            Physician user = physicianManager.login(email, pass);
            if (user != null) {
                dispose();
                controller.showPhysicianApp(user);
                return;
            }

            Receptionist receptionist = receptionistManager.login(email, pass);
            if (receptionist != null) {
                dispose();
                controller.showReceptionistApp(receptionist);
                return;
            }

            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        });

        // Registration logic (show combo box)
        createBtn.addActionListener(e -> {
            String[] userTypes = { "Physician", "Receptionist" };
            JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);

            JDialog dialog = new JDialog(this, "Create Account", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints regGbc = new GridBagConstraints();
            regGbc.insets = new Insets(5, 5, 5, 5);
            regGbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField nameField = new JTextField(20);
            JTextField regEmailField = new JTextField(20);
            JPasswordField passwordField = new JPasswordField(20);
            JPasswordField confirmPasswordField = new JPasswordField(20);

            regGbc.gridx = 0; regGbc.gridy = 0;
            dialog.add(new JLabel("Account Type:"), regGbc);
            regGbc.gridx = 1;
            dialog.add(userTypeCombo, regGbc);

            regGbc.gridx = 0; regGbc.gridy = 1;
            dialog.add(new JLabel("Name:"), regGbc);
            regGbc.gridx = 1;
            dialog.add(nameField, regGbc);

            regGbc.gridx = 0; regGbc.gridy = 2;
            dialog.add(new JLabel("Email:"), regGbc);
            regGbc.gridx = 1;
            dialog.add(regEmailField, regGbc);

            regGbc.gridx = 0; regGbc.gridy = 3;
            dialog.add(new JLabel("Password:"), regGbc);
            regGbc.gridx = 1;
            dialog.add(passwordField, regGbc);

            regGbc.gridx = 0; regGbc.gridy = 4;
            dialog.add(new JLabel("Confirm Password:"), regGbc);
            regGbc.gridx = 1;
            dialog.add(confirmPasswordField, regGbc);

            JButton registerBtn = new JButton("Register");
            registerBtn.setBackground(new Color(76, 175, 80));
            registerBtn.setForeground(Color.WHITE);
            registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            registerBtn.setOpaque(true);
            registerBtn.setBorderPainted(false);
            registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addHoverEffect(registerBtn);

            regGbc.gridx = 0; regGbc.gridy = 5; regGbc.gridwidth = 2; regGbc.anchor = GridBagConstraints.CENTER;
            dialog.add(registerBtn, regGbc);

            registerBtn.addActionListener(ev -> {
                String userType = (String) userTypeCombo.getSelectedItem();
                String name = nameField.getText().trim();
                String email = regEmailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Validation
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (physicianManager.getPhysicianByEmail(email) != null ||
                        receptionistManager.getReceptionistByEmail(email) != null) {
                    JOptionPane.showMessageDialog(dialog, "An account with this email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String id = java.util.UUID.randomUUID().toString();

                if ("Physician".equals(userType)) {
                    Physician newPhysician = new Physician(id, name, email, password);
                    physicianManager.addPhysician(newPhysician);

                    JOptionPane.showMessageDialog(dialog, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    SwingUtilities.invokeLater(() -> controller.showPhysicianApp(newPhysician));
                } else if ("Receptionist".equals(userType)) {
                    Receptionist newReceptionist = new Receptionist(id, name, email, password);
                    receptionistManager.addReceptionist(newReceptionist);

                    JOptionPane.showMessageDialog(dialog, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    SwingUtilities.invokeLater(() -> controller.showReceptionistApp(newReceptionist));
                }
            });

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        setVisible(true);
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color currentColor = button.getBackground();
                button.setBackground(currentColor.brighter());
            }
        });
    }
}