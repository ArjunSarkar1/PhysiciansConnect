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
        List<Notification> storedNotifications = notificationPersistence.getNotificationsForUser(userId, userType);
        for (Notification notification : storedNotifications) {
            notificationListModel.addElement(notification);
            // Only add to unread if it's newer than last viewed time
            if (!notification.isRead() && notification.getTimestamp().isAfter(lastViewedTime)) {
                unreadNotifications.add(notification);
            }
        }
    }

    public void addNotification(String message, String type) {
        Notification notification = new Notification(message, type, LocalDateTime.now(), userId, userType);
        
        // Add to persistence
        notificationPersistence.addNotification(notification);
        
        // Add to the beginning of the list
        notificationListModel.add(0, notification);
        
        // Always add to unread notifications for new notifications
        unreadNotifications.add(notification);
        
        // Keep only the most recent notifications
        while (notificationListModel.size() > MAX_NOTIFICATIONS) {
            notificationListModel.remove(notificationListModel.size() - 1);
        }
    }

    public int getUnreadNotificationCount() {
        // Return the actual count of unread notifications
        return unreadNotifications.size();
    }

    public void markAllAsRead() {
        lastViewedTime = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        unreadNotifications.clear();
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