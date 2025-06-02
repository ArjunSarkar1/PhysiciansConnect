package physicianconnect.presentation;

import physicianconnect.logic.MessageService;
import physicianconnect.objects.Message;
import physicianconnect.objects.Physician;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MessagePanel extends JPanel {
    private final MessageService messageService;
    private final String currentUserId;
    private final JList<Message> messageList;
    private final DefaultListModel<Message> messageListModel;
    private final JTextField messageInput;
    private final JTextField searchField;
    private final JList<Physician> searchResultsList;
    private final DefaultListModel<Physician> searchResultsModel;
    private final JLabel unreadCountLabel;
    private final List<Physician> allPhysicians;
    private Physician selectedRecipient;
    private final JLabel selectedRecipientLabel;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a");

    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(34, 40, 49);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public MessagePanel(MessageService messageService, String currentUserId, List<Physician> physicians) {
        this.messageService = messageService;
        this.currentUserId = currentUserId;
        this.allPhysicians = physicians.stream()
            .filter(p -> !p.getId().equals(currentUserId)) // Exclude current user
            .collect(Collectors.toList());
        
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Messages");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        unreadCountLabel = new JLabel();
        unreadCountLabel.setFont(LABEL_FONT);
        unreadCountLabel.setForeground(TEXT_COLOR);
        headerPanel.add(unreadCountLabel, BorderLayout.EAST);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Search Recipient: ");
        searchLabel.setFont(LABEL_FONT);
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = new JTextField();
        searchField.setFont(LABEL_FONT);
        searchField.putClientProperty("JTextField.placeholderText", "Type name or email to search...");
        
        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(searchField.getText()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(searchField.getText()); }
        });
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Selected Recipient Label
        selectedRecipientLabel = new JLabel("No recipient selected");
        selectedRecipientLabel.setFont(LABEL_FONT);
        selectedRecipientLabel.setForeground(TEXT_COLOR);
        selectedRecipientLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Search Results List
        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setFont(LABEL_FONT);
        searchResultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Physician) {
                    Physician p = (Physician) value;
                    // Only show unread count for physicians who have sent messages to current user
                    List<Message> unreadMessages = messageService.getUnreadMessagesForUser(currentUserId).stream()
                        .filter(m -> m.getSenderId().equals(p.getId()))
                        .collect(Collectors.toList());
                    String unreadText = !unreadMessages.isEmpty() ? " (" + unreadMessages.size() + " unread)" : "";
                    setText(p.getName() + " (" + p.getEmail() + ")" + unreadText);
                }
                return this;
            }
        });

        // Add selection listener
        searchResultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Physician newSelection = searchResultsList.getSelectedValue();
                if (newSelection != null) {
                    selectedRecipient = newSelection;
                    selectedRecipientLabel.setText("Selected: " + selectedRecipient.getName());
                    refreshMessages();
                }
            }
        });

        // Add mouse listener to handle clicks
        searchResultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = searchResultsList.locationToIndex(e.getPoint());
                if (index != -1) {
                    searchResultsList.setSelectedIndex(index);
                    Physician clickedPhysician = searchResultsList.getModel().getElementAt(index);
                    selectedRecipient = clickedPhysician;
                    selectedRecipientLabel.setText("Selected: " + selectedRecipient.getName());
                    refreshMessages();
                }
            }
        });

        JScrollPane searchScrollPane = new JScrollPane(searchResultsList);
        searchScrollPane.setPreferredSize(new Dimension(400, 150));
        searchScrollPane.setBorder(BorderFactory.createTitledBorder("All Physicians"));

        // Message List
        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);
        messageList.setCellRenderer(new MessageCellRenderer());
        messageList.setFont(LABEL_FONT);
        messageList.setBackground(Color.WHITE);
        messageList.setFixedCellHeight(60); // Set fixed height for each message
        JScrollPane messageScrollPane = new JScrollPane(messageList);
        messageScrollPane.setPreferredSize(new Dimension(400, 300));
        messageScrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        messageInput = new JTextField();
        messageInput.setFont(LABEL_FONT);
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.setFont(BUTTON_FONT);
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(true);
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Create a panel for the search and results
        JPanel searchContainer = new JPanel(new BorderLayout(5, 5));
        searchContainer.setBackground(BACKGROUND_COLOR);
        searchContainer.add(searchPanel, BorderLayout.NORTH);
        searchContainer.add(searchScrollPane, BorderLayout.CENTER);
        searchContainer.add(selectedRecipientLabel, BorderLayout.SOUTH);

        // Create a panel for the messages
        JPanel messageContainer = new JPanel(new BorderLayout(5, 5));
        messageContainer.setBackground(BACKGROUND_COLOR);
        messageContainer.add(messageScrollPane, BorderLayout.CENTER);
        messageContainer.add(inputPanel, BorderLayout.SOUTH);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(searchContainer, BorderLayout.WEST);
        add(messageContainer, BorderLayout.CENTER);

        // Show all physicians by default
        showAllPhysicians();
    }

    private void showAllPhysicians() {
        searchResultsModel.clear();
        allPhysicians.forEach(searchResultsModel::addElement);
    }

    private void filter(String searchText) {
        searchResultsModel.clear();
        if (searchText.isEmpty()) {
            showAllPhysicians();
        } else {
            allPhysicians.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchText.toLowerCase()) || 
                           p.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                .forEach(searchResultsModel::addElement);
        }
    }

    private void refreshMessages() {
        messageListModel.clear();
        if (selectedRecipient != null) {
            // Get all messages from backend
            List<Message> messages = messageService.getMessagesForUser(currentUserId);
            
            // Filter messages to show only conversation with selected recipient
            List<Message> conversationMessages = messages.stream()
                .filter(m -> m.getSenderId().equals(selectedRecipient.getId()) || 
                           m.getReceiverId().equals(selectedRecipient.getId()))
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp())) // Sort by timestamp (oldest first)
                .collect(Collectors.toList());
            
            // Add messages to the list in chronological order
            conversationMessages.forEach(messageListModel::addElement);
            
            // Mark messages as read only if they were sent by the selected recipient
            conversationMessages.stream()
                .filter(m -> m.getReceiverId().equals(currentUserId) && 
                           m.getSenderId().equals(selectedRecipient.getId()) && 
                           !m.isRead())
                .forEach(m -> {
                    messageService.markMessageAsRead(m.getMessageId());
                    m.setRead(true); // Update local state
                });
            
            // Ensure the latest message is visible
            scrollToBottom();
        }
        
        // Update unread count and refresh physician list to show unread counts
        updateUnreadCount();
    }

    private void updateUnreadCount() {
        int unreadCount = messageService.getUnreadMessageCount(currentUserId);
        unreadCountLabel.setText(unreadCount > 0 ? unreadCount + " unread" : "");
        showAllPhysicians(); // Refresh the list to update unread counts
    }

    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (!content.isEmpty() && selectedRecipient != null) {
            // Save message to backend
            Message sentMessage = messageService.sendMessage(currentUserId, selectedRecipient.getId(), content);
            messageInput.setText("");
            
            // Add the message directly to the list
            messageListModel.addElement(sentMessage);
            
            // Ensure the new message is visible
            scrollToBottom();
            
            // Update unread count
            updateUnreadCount();
        } else if (selectedRecipient == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a recipient first",
                "No Recipient Selected",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void scrollToBottom() {
        if (messageListModel.getSize() > 0) {
            messageList.ensureIndexIsVisible(messageListModel.getSize() - 1);
            // Force scroll to bottom
            JScrollPane scrollPane = (JScrollPane) messageList.getParent().getParent();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        }
    }

    private class MessageCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Message) {
                Message message = (Message) value;
                boolean isSent = message.getSenderId().equals(currentUserId);
                
                String timestamp = message.getTimestamp().format(TIME_FORMATTER);
                // Only show read status for sent messages
                String status = isSent ? (message.isRead() ? "✓✓" : "✓") : "";
                
                // Create a styled message with proper alignment and spacing
                setText(String.format("<html><div style='width: 100%%; padding: 5px;'><b>%s</b> (%s) %s<br>%s</div></html>", 
                    isSent ? "You" : selectedRecipient.getName(),
                    timestamp,
                    status,
                    message.getContent()));
                
                // Align messages based on sender
                setHorizontalAlignment(isSent ? SwingConstants.RIGHT : SwingConstants.LEFT);
                
                // Add padding and set background color
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (!isSelected) {
                    setBackground(isSent ? new Color(220, 248, 198) : Color.WHITE);
                }
                
                // Set maximum width for messages to create a chat-like appearance
                setPreferredSize(new Dimension(list.getWidth() - 20, getPreferredSize().height));
            }
            
            return this;
        }
    }
} 