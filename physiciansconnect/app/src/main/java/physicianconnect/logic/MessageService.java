package physicianconnect.logic;

import physicianconnect.objects.Message;
import physicianconnect.persistence.MessageRepository;
import java.util.List;
import java.util.UUID;

public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(String senderId, String receiverId, String content) {
        Message message = new Message(senderId, receiverId, content);
        return messageRepository.save(message);
    }

    public List<Message> getMessagesForUser(String userId) {
        // Get both sent and received messages
        List<Message> receivedMessages = messageRepository.findByReceiverId(userId);
        List<Message> sentMessages = messageRepository.findBySenderId(userId);
        
        // Combine and sort by timestamp
        List<Message> allMessages = new java.util.ArrayList<>();
        allMessages.addAll(receivedMessages);
        allMessages.addAll(sentMessages);
        allMessages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
        
        return allMessages;
    }

    public List<Message> getUnreadMessagesForUser(String userId) {
        return messageRepository.findUnreadByReceiverId(userId);
    }

    public void markMessageAsRead(UUID messageId) {
        messageRepository.markAsRead(messageId);
    }

    public int getUnreadMessageCount(String userId) {
        return messageRepository.countUnreadMessages(userId);
    }
} 