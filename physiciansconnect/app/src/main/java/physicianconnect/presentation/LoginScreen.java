package physicianconnect.presentation;

import physicianconnect.logic.AppointmentManager;
import physicianconnect.logic.PhysicianManager;
import physicianconnect.objects.Physician;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen(PhysicianManager physicianManager, AppointmentManager appointmentManager) {
        setTitle("PhysicianConnect Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        emailField.setName("emailField");

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);
        passField.setName("passwordField");

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");
        loginBtn.setName("loginBtn");
        createBtn.setName("createBtn");

        JLabel testInfo = new JLabel("Test login: test@email.com / test123");
        testInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        testInfo.setForeground(new Color(0, 0, 0));

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

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            Physician user = physicianManager.login(email, pass);
            if (user != null) {
                dispose(); // close login screen
                PhysicianApp.launchSingleUser(user, physicianManager, appointmentManager);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        createBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Name:");
            String email = JOptionPane.showInputDialog("Email:");
            String password = JOptionPane.showInputDialog("Password:");

            if (name != null && email != null && password != null) {
                String id = java.util.UUID.randomUUID().toString();
                physicianManager.addPhysician(new Physician(id, name, email, password));
                JOptionPane.showMessageDialog(this, "Account created!");
            }
        });

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(testInfo);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passLabel);
        panel.add(passField);

        JPanel buttons = new JPanel();
        buttons.add(loginBtn);
        buttons.add(createBtn);

        add(panel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
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
