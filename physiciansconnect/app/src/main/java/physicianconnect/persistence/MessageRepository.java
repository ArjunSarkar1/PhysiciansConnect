package physicianconnect.persistence;

import physicianconnect.objects.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findByReceiverId(String receiverId);
    List<Message> findBySenderId(String senderId);
    List<Message> findUnreadByReceiverId(String receiverId);
    void markAsRead(UUID messageId);
    int countUnreadMessages(String receiverId);
} 