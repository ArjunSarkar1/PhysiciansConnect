package physicianconnect.presentation;

import javax.swing.SwingUtilities;

public class PhysicianApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PhysicianConnectUI();
        });
    }
}