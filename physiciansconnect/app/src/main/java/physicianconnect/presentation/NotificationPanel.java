package physicianconnect.presentation;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import physicianconnect.presentation.config.UITheme;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;

public class NotificationPanel extends JPanel {
    private final DefaultListModel<Notification> notificationListModel;
    private final JList<Notification> notificationList;
    private static final int MAX_NOTIFICATIONS = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a");
    private final NotificationPersistence notificationPersistence;
    private final String userId;
    private final String userType;
    private final List<Notification> unreadNotifications;
    private LocalDateTime lastViewedTime;

    public NotificationPanel(NotificationPersistence notificationPersistence, String userId, String userType) {
        this.notificationPersistence = notificationPersistence;
        this.userId = userId;
        this.userType = userType;
        this.unreadNotifications = new ArrayList<>();
        this.lastViewedTime = LocalDateTime.now();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel titleLabel = new JLabel("Recent Notifications");
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Notification list
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setCellRenderer(new NotificationCellRenderer());
        notificationList.setBackground(UITheme.BACKGROUND_COLOR);
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Load existing notifications
        loadNotifications();
    }

    /**
     * Load notifications from persistence
     */
    public void loadNotifications() {
        notificationListModel.clear();
        unreadNotifications.clear();
        
        // Get all notifications for the user
        List<Notification> storedNotifications = notificationPersistence.getNotificationsForUser(userId, userType);
        
        // Add notifications to the list model
        for (Notification notification : storedNotifications) {
            notificationListModel.addElement(notification);
            if (!notification.isRead()) {
                unreadNotifications.add(notification);
            }
        }
        
        // Get the count of unread notifications from persistence
        int unreadCount = notificationPersistence.getUnreadNotificationCount(userId, userType);
        
        // Update the notification counter
        updateNotificationCount(unreadCount);
    }

    public void addNotification(String message, String type) {
        // Check if a similar notification already exists in the last few seconds
        LocalDateTime now = LocalDateTime.now();
        boolean duplicateExists = false;
        
        // Check both in-memory list and database for duplicates
        for (int i = 0; i < notificationListModel.size(); i++) {
            Notification existing = notificationListModel.get(i);
            if (existing.getMessage().equals(message) && 
                existing.getType().equals(type) && 
                existing.getTimestamp().isAfter(now.minusSeconds(5))) {
                duplicateExists = true;
                break;
            }
        }
        
        if (!duplicateExists) {
            Notification notification = new Notification(message, type, now, userId, userType);
            
            // Add to persistence
            notificationPersistence.addNotification(notification);
            
            // Reload all notifications to ensure consistency
            loadNotifications();
        }
    }

    public int getUnreadNotificationCount() {
        // Return the actual count of unread notifications
        return unreadNotifications.size();
    }

    public void markAllAsRead() {
        lastViewedTime = LocalDateTime.now();
        notificationPersistence.markNotificationsAsRead(userId, userType);
        // Reload notifications to ensure UI is in sync with database
        loadNotifications();
    }

    private void updateNotificationCount(int count) {
        // This method should be called by the parent component to update the notification counter
        if (getParent() != null) {
            Container parent = getParent();
            while (parent != null && !(parent instanceof NotificationButton)) {
                parent = parent.getParent();
            }
            if (parent instanceof NotificationButton) {
                final NotificationButton button = (NotificationButton) parent;
                final int finalCount = count;
                SwingUtilities.invokeLater(() -> {
                    button.updateNotificationCount(finalCount);
                });
            }
        }
    }

    private class NotificationCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Notification) {
                Notification notification = (Notification) value;
                String typeColor;
                switch (notification.getType()) {
                    case "Appointment Cancellation!":
                        typeColor = "#FF0000"; // Red
                        break;
                    case "Appointment Update!":
                        typeColor = "#FFA500"; // Orange
                        break;
                    case "New Prescription!":
                        typeColor = "#00008B"; // Dark Blue
                        break;
                    case "New Referral!":
                        typeColor = "#800080"; // Purple
                        break;
                    default:
                        typeColor = "#2E7D32"; // Default green
                }
                
                setText(String.format("<html><div style='width: 100%%; padding: 5px;'>" +
                        "<b style='color: %s;'>%s</b><br>" +
                        "<span style='color: black;'>%s</span><br>" +
                        "<i style='color: #666;'>%s</i></div></html>",
                        typeColor,
                        notification.getType(),
                        notification.getMessage(),
                        notification.getTimestamp().format(TIME_FORMATTER)));
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                
                if (isSelected) {
                    setBackground(new Color(240, 240, 240)); // Light gray for selection
                } else {
                    setBackground(Color.WHITE);
                }
            }
            return this;
        }
    }
} 