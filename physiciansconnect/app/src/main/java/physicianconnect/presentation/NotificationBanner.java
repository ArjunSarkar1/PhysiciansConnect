package physicianconnect.presentation;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import physicianconnect.presentation.config.UITheme;

public class NotificationBanner extends JWindow {
    private static final int DISPLAY_TIME = 4000; // 4 seconds
    private final Timer dismissTimer;
    private final JLabel messageLabel;
    private ActionListener clickListener;

    public NotificationBanner(Window owner) {
        super(owner);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setAlwaysOnTop(true);

        // Add a border
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        setContentPane(contentPanel);

        // Message label
        messageLabel = new JLabel();
        messageLabel.setFont(UITheme.LABEL_FONT);
        messageLabel.setForeground(Color.BLACK);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Dismiss timer
        dismissTimer = new Timer(DISPLAY_TIME, e -> dismiss());
        dismissTimer.setRepeats(false);

        // Make banner clickable
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (clickListener != null) {
                    clickListener.actionPerformed(null);
                }
                dismiss();
            }
        });

        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                contentPanel.setBackground(new Color(245, 245, 245)); // Light gray on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                contentPanel.setBackground(Color.WHITE);
            }
        });
    }

    public void show(String message, ActionListener onClick) {
        messageLabel.setText(message);
        this.clickListener = onClick;
        
        // Calculate position (top center of the owner window)
        Window owner = getOwner();
        if (owner != null) {
            Dimension ownerSize = owner.getSize();
            Point ownerLocation = owner.getLocation();
            
            pack();
            Dimension bannerSize = getSize();
            
            int x = ownerLocation.x + (ownerSize.width - bannerSize.width) / 2;
            int y = ownerLocation.y + 20; // 20 pixels from top
            
            setLocation(x, y);
        }
        
        setVisible(true);
        dismissTimer.restart();
    }

    public void dismiss() {
        setVisible(false);
        dismissTimer.stop();
    }
} 