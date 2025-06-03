package physicianconnect.presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MessageButton extends JPanel {
    private final JButton messageButton;
    private final JLabel notificationLabel;
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color NOTIFICATION_COLOR = new Color(244, 67, 54);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public MessageButton() {
        setLayout(new OverlayLayout(this));
        setOpaque(false);

        // Create message button
        messageButton = new JButton("💬");
        messageButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        messageButton.setBackground(PRIMARY_COLOR);
        messageButton.setForeground(Color.WHITE);
        messageButton.setFocusPainted(false);
        messageButton.setBorderPainted(false);
        messageButton.setOpaque(true);
        messageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Create notification label
        notificationLabel = new JLabel();
        notificationLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        notificationLabel.setForeground(Color.WHITE);
        notificationLabel.setBackground(NOTIFICATION_COLOR);
        notificationLabel.setOpaque(true);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        notificationLabel.setVisible(false);

        // Position notification label
        JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        notificationPanel.setOpaque(false);
        notificationPanel.add(notificationLabel);

        add(messageButton);
        add(notificationPanel);

        // Add hover effect
        messageButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                messageButton.setBackground(PRIMARY_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                messageButton.setBackground(PRIMARY_COLOR);
            }
        });
    }

    public void setOnAction(ActionListener listener) {
        messageButton.addActionListener(listener);
    }

    public void updateNotificationCount(int count) {
        if (count > 0) {
            notificationLabel.setText(String.valueOf(count));
            notificationLabel.setVisible(true);
        } else {
            notificationLabel.setVisible(false);
        }
    }
} 