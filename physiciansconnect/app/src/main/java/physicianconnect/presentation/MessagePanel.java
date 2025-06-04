package physicianconnect.presentation;

import physicianconnect.logic.MessageService;
import physicianconnect.objects.Message;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

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
    private final String currentUserType; // "physician" or "receptionist"
    private final JList<Message> messageList;
    private final DefaultListModel<Message> messageListModel;
    private final JTextField messageInput;
    private final JTextField searchField;
    private final JList<Object> searchResultsList;
    private final DefaultListModel<Object> searchResultsModel;
    private final JLabel unreadCountLabel;
    private final List<Object> allUsers; // Physician or Receptionist
    private Object selectedRecipient;
    private final JLabel selectedRecipientLabel;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a");

    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(34, 40, 49);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public MessagePanel(MessageService messageService, String currentUserId, String currentUserType,
            List<Object> users) {
        this.currentUserId = currentUserId;
        this.currentUserType = currentUserType;
        this.messageService = messageService;
        this.allUsers = users.stream()
                .filter(u -> !(getUserId(u).equals(currentUserId) && getUserType(u).equals(currentUserType)))
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
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter(searchField.getText());
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter(searchField.getText());
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter(searchField.getText());
            }
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
                String name = getUserName(value);
                String email = getUserEmail(value);
                // Show unread count for this user
                List<Message> unreadMessages = messageService.getUnreadMessagesForUser(currentUserId, currentUserType)
                        .stream()
                        .filter(m -> m.getSenderId().equals(getUserId(value))
                                && m.getSenderType().equals(getUserType(value)))
                        .collect(Collectors.toList());
                String unreadText = !unreadMessages.isEmpty() ? " (" + unreadMessages.size() + " unread)" : "";
                setText(name + " (" + email + ")" + unreadText);
                return this;
            }
        });

        // Add selection listener
        searchResultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Object newSelection = searchResultsList.getSelectedValue();
                if (newSelection != null) {
                    selectedRecipient = newSelection;
                    selectedRecipientLabel.setText("Selected: " + getUserName(selectedRecipient));
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
                    Object clickedUser = searchResultsList.getModel().getElementAt(index);
                    selectedRecipient = clickedUser;
                    selectedRecipientLabel.setText("Selected: " + getUserName(selectedRecipient));
                    refreshMessages();
                }
            }
        });

        JScrollPane searchScrollPane = new JScrollPane(searchResultsList);
        searchScrollPane.setPreferredSize(new Dimension(400, 150));
        searchScrollPane.setBorder(BorderFactory.createTitledBorder("All Users"));

        // Message List
        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);
        messageList.setCellRenderer(new MessageCellRenderer());
        messageList.setFont(LABEL_FONT);
        messageList.setBackground(Color.WHITE);
        messageList.setFixedCellHeight(60);
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

        // Show all users by default
        showAllUsers();
    }

    private void showAllUsers() {
        searchResultsModel.clear();
        allUsers.forEach(searchResultsModel::addElement);
    }

    private void filter(String searchText) {
        searchResultsModel.clear();
        if (searchText.isEmpty()) {
            showAllUsers();
        } else {
            allUsers.stream()
                    .filter(u -> getUserName(u).toLowerCase().contains(searchText.toLowerCase()) ||
                            getUserEmail(u).toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(searchResultsModel::addElement);
        }
    }

    private void refreshMessages() {
        messageListModel.clear();
        if (selectedRecipient != null) {
            List<Message> messages = messageService.getMessagesForUser(currentUserId, currentUserType);
            String recipientId = getUserId(selectedRecipient);
            List<Message> conversationMessages = messages.stream()
                    .filter(m -> ((m.getSenderId().equals(currentUserId) && m.getSenderType().equals(currentUserType) &&
                            m.getReceiverId().equals(getUserId(selectedRecipient))
                            && m.getReceiverType().equals(getUserType(selectedRecipient)))
                            ||
                            (m.getReceiverId().equals(currentUserId) && m.getReceiverType().equals(currentUserType) &&
                                    m.getSenderId().equals(getUserId(selectedRecipient))
                                    && m.getSenderType().equals(getUserType(selectedRecipient)))))
                    .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                    .collect(Collectors.toList());

            conversationMessages.forEach(messageListModel::addElement);

            // Mark messages as read only if they were sent by the selected recipient
            conversationMessages.stream()
                    .filter(m -> m.getReceiverId().equals(currentUserId) &&
                            m.getSenderId().equals(recipientId) &&
                            !m.isRead())
                    .forEach(m -> {
                        messageService.markMessageAsRead(m.getMessageId());
                        m.setRead(true);
                    });

            scrollToBottom();
        }
        updateUnreadCount();
    }

    private void updateUnreadCount() {
        int unreadCount = messageService.getUnreadMessageCount(currentUserId, currentUserType);
        unreadCountLabel.setText(unreadCount > 0 ? unreadCount + " unread" : "");
        showAllUsers();
    }

    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (!content.isEmpty() && selectedRecipient != null) {
            Message sentMessage = messageService.sendMessage(
                    currentUserId,
                    currentUserType,
                    getUserId(selectedRecipient),
                    getUserType(selectedRecipient),
                    content);
            messageInput.setText("");
            messageListModel.addElement(sentMessage);
            scrollToBottom();
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
            JScrollPane scrollPane = (JScrollPane) messageList.getParent().getParent();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        }
    }

    // Helper methods for user info
    private String getUserId(Object user) {
        if (user instanceof Physician p)
            return p.getId();
        if (user instanceof Receptionist r)
            return r.getId();
        return "";
    }

    private String getUserName(Object user) {
        if (user instanceof Physician p)
            return p.getName();
        if (user instanceof Receptionist r)
            return r.getName();
        return "";
    }

    private String getUserEmail(Object user) {
        if (user instanceof Physician p)
            return p.getEmail();
        if (user instanceof Receptionist r)
            return r.getEmail();
        return "";
    }

    private String getUserType(Object user) {
        if (user instanceof Physician)
            return "physician";
        if (user instanceof Receptionist)
            return "receptionist";
        return "";
    }

    private String getUserName(String id, String type) {
        for (Object user : allUsers) {
            if (getUserId(user).equals(id) && getUserType(user).equals(type)) {
                return getUserName(user);
            }
        }
        return id;
    }

    private class MessageCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Message) {
                Message message = (Message) value;
                boolean isSent = message.getSenderId().equals(currentUserId)
                        && message.getSenderType().equals(currentUserType);

                String timestamp = message.getTimestamp().format(TIME_FORMATTER);
                String status = isSent ? (message.isRead() ? "✓✓" : "✓") : "";

                setText(String.format(
                        "<html><div style='width: 100%%; padding: 5px;'><b>%s</b> (%s) %s<br>%s</div></html>",
                        isSent ? "You" : getUserName(message.getSenderId(), message.getSenderType()),
                        timestamp,
                        status,
                        message.getContent()));

                setHorizontalAlignment(isSent ? SwingConstants.RIGHT : SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (!isSelected) {
                    setBackground(isSent ? new Color(220, 248, 198) : Color.WHITE);
                }
                setPreferredSize(new Dimension(list.getWidth() - 20, getPreferredSize().height));
            }
            return this;
        }
    }
}