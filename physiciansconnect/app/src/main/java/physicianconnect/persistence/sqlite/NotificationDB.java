package physicianconnect.persistence.sqlite;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;

public class NotificationDB implements NotificationPersistence {
    private final Connection conn;

    public NotificationDB(Connection conn) {
        this.conn = conn;
        createTable();
    }

    private void createTable() {
        // First drop the existing table if it exists
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS notifications");
        } catch (SQLException e) {
            System.err.println("Error dropping notifications table: " + e.getMessage());
            e.printStackTrace();
        }

        // Create the table with the correct schema
        String sql = """
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                user_type TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                is_read BOOLEAN NOT NULL DEFAULT 0
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Successfully created notifications table with is_read column");
        } catch (SQLException e) {
            System.err.println("Error creating notifications table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void addNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, user_type, message, type, timestamp, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notification.getUserId());
            pstmt.setString(2, notification.getUserType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setString(4, notification.getType());
            pstmt.setString(5, notification.getTimestamp().toString());
            pstmt.setBoolean(6, notification.isRead());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Added notification: " + notification.getMessage() + " (rows affected: " + rowsAffected + ")");
            
            // Keep only the most recent notifications
            String deleteSql = "DELETE FROM notifications WHERE id NOT IN (SELECT id FROM notifications WHERE user_id = ? AND user_type = ? ORDER BY timestamp DESC LIMIT ?)";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, notification.getUserId());
                deleteStmt.setString(2, notification.getUserType());
                deleteStmt.setInt(3, 10); // Keep only the 10 most recent notifications
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId, String userType) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND user_type = ? ORDER BY timestamp DESC LIMIT 10";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification(
                        rs.getString("message"),
                        rs.getString("type"),
                        LocalDateTime.parse(rs.getString("timestamp")),
                        rs.getString("user_id"),
                        rs.getString("user_type")
                    );
                    notification.setRead(rs.getBoolean("is_read"));
                    notifications.add(notification);
                }
            }
            System.out.println("Retrieved " + notifications.size() + " notifications for user " + userId);
        } catch (SQLException e) {
            System.err.println("Error getting notifications: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }

    @Override
    public void clearNotificationsForUser(String userId, String userType) {
        String sql = "DELETE FROM notifications WHERE user_id = ? AND user_type = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userType);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Cleared " + rowsAffected + " notifications for user " + userId);
        } catch (SQLException e) {
            System.err.println("Error clearing notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getUnreadNotificationCount(String userId, String userType) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND user_type = ? AND is_read = 0";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Unread notification count for user " + userId + ": " + count);
                    return count;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting unread notifications: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public void markNotificationsAsRead(String userId, String userType) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND user_type = ? AND is_read = 0";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userType);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Marked " + rowsAffected + " notifications as read for user " + userId);
        } catch (SQLException e) {
            System.err.println("Error marking notifications as read: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Marked notification " + notificationId + " as read (rows affected: " + rowsAffected + ")");
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 