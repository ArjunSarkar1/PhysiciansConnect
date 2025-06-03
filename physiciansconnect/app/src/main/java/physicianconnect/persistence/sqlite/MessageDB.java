package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Message;
import physicianconnect.persistence.MessageRepository;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class MessageDB implements MessageRepository {
    private final Connection connection;

    public MessageDB(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    message_id TEXT PRIMARY KEY,
                    sender_id TEXT NOT NULL,
                    receiver_id TEXT NOT NULL,
                    content TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    is_read BOOLEAN NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create messages table: " + e.getMessage(), e);
        }
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (message.getMessageId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }

        String sql = "INSERT OR REPLACE INTO messages (message_id, sender_id, receiver_id, content, timestamp, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, message.getMessageId().toString());
            pstmt.setString(2, message.getSenderId());
            pstmt.setString(3, message.getReceiverId());
            pstmt.setString(4, message.getContent());
            pstmt.setString(5, message.getTimestamp().toString());
            pstmt.setBoolean(6, message.isRead());
            pstmt.executeUpdate();
            return message;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save message: " + e.getMessage() + 
                " (Message ID: " + message.getMessageId() + ")", e);
        }
    }

    @Override
    public List<Message> findByReceiverId(String receiverId) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }

        String sql = "SELECT * FROM messages WHERE receiver_id = ? ORDER BY timestamp";
        return queryMessages(sql, receiverId, "Failed to find messages for receiver: " + receiverId);
    }

    @Override
    public List<Message> findBySenderId(String senderId) {
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender ID cannot be null or empty");
        }

        String sql = "SELECT * FROM messages WHERE sender_id = ? ORDER BY timestamp";
        return queryMessages(sql, senderId, "Failed to find messages from sender: " + senderId);
    }

    @Override
    public List<Message> findUnreadByReceiverId(String receiverId) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }

        String sql = "SELECT * FROM messages WHERE receiver_id = ? AND is_read = 0 ORDER BY timestamp";
        return queryMessages(sql, receiverId, "Failed to find unread messages for receiver: " + receiverId);
    }

    @Override
    public void markAsRead(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }

        String sql = "UPDATE messages SET is_read = 1 WHERE message_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, messageId.toString());
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("No message found with ID: " + messageId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark message as read: " + e.getMessage() + 
                " (Message ID: " + messageId + ")", e);
        }
    }

    @Override
    public int countUnreadMessages(String receiverId) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }

        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, receiverId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count unread messages: " + e.getMessage() + 
                " (Receiver ID: " + receiverId + ")", e);
        }
    }

    private List<Message> queryMessages(String sql, String userId, String errorMessage) {
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    Message message = new Message(
                        rs.getString("sender_id"),
                        rs.getString("receiver_id"),
                        rs.getString("content")
                    );
                    message.setMessageId(UUID.fromString(rs.getString("message_id")));
                    message.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                    message.setRead(rs.getBoolean("is_read"));
                    messages.add(message);
                } catch (SQLException e) {
                    System.err.println("Error reading message from result set: " + e.getMessage());
                    // Continue processing other messages
                }
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(errorMessage + ": " + e.getMessage(), e);
        }
    }
} 