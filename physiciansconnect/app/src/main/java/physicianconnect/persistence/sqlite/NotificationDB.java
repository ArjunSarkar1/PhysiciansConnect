package physicianconnect.persistence.sqlite;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

public class NotificationDB implements NotificationPersistence {
    private final Connection conn;
    private final ReceptionistPersistence receptionistPersistence;

    public NotificationDB(Connection conn, ReceptionistPersistence receptionistPersistence) {
        this.conn = conn;
        this.receptionistPersistence = receptionistPersistence;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                user_type TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT NOT NULL,
                timestamp TEXT NOT NULL
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, user_type, message, type, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notification.getUserId());
            pstmt.setString(2, notification.getUserType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setString(4, notification.getType());
            pstmt.setString(5, notification.getTimestamp().toString());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
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
                    notifications.add(new Notification(
                        rs.getString("message"),
                        rs.getString("type"),
                        LocalDateTime.parse(rs.getString("timestamp")),
                        rs.getString("user_id"),
                        rs.getString("user_type")
                    ));
                }
            }
        } catch (SQLException e) {
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
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markNotificationAsRead(Notification notification) {
        // Since we're using SQLite and the Notification object is immutable,
        // we'll just mark it as read in memory
        notification.markAsRead();
    }

    public void broadcastToReceptionists(String message, String type) {
        // Get all receptionists
        List<String> receptionistIds = receptionistPersistence.getAllReceptionistIds();
        
        // Create a notification for each receptionist
        for (String receptionistId : receptionistIds) {
            Notification notification = new Notification(
                message,
                type,
                LocalDateTime.now(),
                receptionistId,
                "receptionist"
            );
            addNotification(notification);
        }
    }
} 