package physicianconnect.persistence;

import physicianconnect.objects.Message;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new ConcurrentHashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getMessageId(), message);
        return message;
    }

    @Override
    public List<Message> findByReceiverId(String receiverId) {
        return messages.values().stream()
                .filter(message -> message.getReceiverId().equals(receiverId))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySenderId(String senderId) {
        return messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findUnreadByReceiverId(String receiverId) {
        return messages.values().stream()
                .filter(message -> message.getReceiverId().equals(receiverId) && !message.isRead())
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(UUID messageId) {
        Message message = messages.get(messageId);
        if (message != null) {
            message.setRead(true);
            messages.put(messageId, message);
        }
    }

    @Override
    public int countUnreadMessages(String receiverId) {
        return (int) messages.values().stream()
                .filter(message -> message.getReceiverId().equals(receiverId) && !message.isRead())
                .count();
    }
} 