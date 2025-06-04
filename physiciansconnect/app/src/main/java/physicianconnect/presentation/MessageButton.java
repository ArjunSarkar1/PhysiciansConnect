package physicianconnect.presentation;

import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MessageButton extends JPanel {
    private final JButton messageButton;
    private final JLabel notificationLabel;

    public MessageButton() {
        setLayout(new OverlayLayout(this));
        setOpaque(false);

        // Create message button
        messageButton = new JButton("Messages 💬");
        messageButton.setFont(BUTTON_FONT);
        messageButton.setBackground(PRIMARY_COLOR);
        messageButton.setForeground(Color.WHITE);
        messageButton.setFocusPainted(false);
        messageButton.setBorderPainted(false);
        messageButton.setOpaque(true);
        messageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Create notification label
        notificationLabel = new JLabel();
        notificationLabel.setFont(UITheme.NOTIFICATION_FONT);
        notificationLabel.setForeground(UITheme.BACKGROUND_COLOR);
        notificationLabel.setBackground(UITheme.ERROR_COLOR);
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
        UITheme.applyHoverEffect(messageButton);
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