// File: physicianconnect/presentation/MessagePanel.java

package physicianconnect.presentation;

import physicianconnect.logic.controller.MessageController;
import physicianconnect.logic.exceptions.InvalidMessageException;
import physicianconnect.objects.Message;
import physicianconnect.objects.Physician;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MessagePanel extends JPanel {
    private final MessageController messageController;
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

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(UIConfig.TIME_FORMAT_PATTERN);

    public MessagePanel(MessageController messageController,
                        String currentUserId,
                        List<Physician> physicians) {
        this.messageController = messageController;
        this.currentUserId     = currentUserId;
        // Exclude the current user from the recipient list
        this.allPhysicians = physicians.stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .collect(Collectors.toList());

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ─────────── Header Panel ───────────
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(UIConfig.MESSAGES_TITLE);
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        unreadCountLabel = new JLabel();
        unreadCountLabel.setFont(UITheme.LABEL_FONT);
        unreadCountLabel.setForeground(UITheme.TEXT_COLOR);
        headerPanel.add(unreadCountLabel, BorderLayout.EAST);

        // ─────────── Search Panel ───────────
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel(UIConfig.SEARCH_RECIPIENT_LABEL);
        searchLabel.setFont(UITheme.LABEL_FONT);
        searchLabel.setForeground(UITheme.TEXT_COLOR);

        searchField = new JTextField();
        searchField.setFont(UITheme.LABEL_FONT);
        searchField.putClientProperty("JTextField.placeholderText", UIConfig.SEARCH_PLACEHOLDER);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(searchField.getText()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(searchField.getText()); }
        });

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // ─────────── Selected Recipient Label ───────────
        selectedRecipientLabel = new JLabel(UIConfig.NO_RECIPIENT_SELECTED);
        selectedRecipientLabel.setFont(UITheme.LABEL_FONT);
        selectedRecipientLabel.setForeground(UITheme.TEXT_COLOR);
        selectedRecipientLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // ─────────── Search Results List ───────────
        searchResultsModel = new DefaultListModel<>();
        searchResultsList  = new JList<>(searchResultsModel);
        searchResultsList.setFont(UITheme.LABEL_FONT);
        searchResultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Physician) {
                    Physician p = (Physician) value;
                    int unreadMessages = (int) messageController
                            .getUnreadMessagesForUser(currentUserId)
                            .stream()
                            .filter(m -> m.getSenderId().equals(p.getId()))
                            .count();
                    String unreadText = unreadMessages > 0
                            ? " (" + unreadMessages + " unread)"
                            : "";
                    setText(p.getName() + " (" + p.getEmail() + ")" + unreadText);
                }
                return this;
            }
        });

        searchResultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Physician newSelection = searchResultsList.getSelectedValue();
                if (newSelection != null) {
                    selectedRecipient = newSelection;
                    selectedRecipientLabel.setText(UIConfig.SELECTED_PREFIX + selectedRecipient.getName());
                    refreshMessages();
                }
            }
        });

        searchResultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = searchResultsList.locationToIndex(e.getPoint());
                if (index != -1) {
                    searchResultsList.setSelectedIndex(index);
                    Physician clickedPhysician = searchResultsList.getModel().getElementAt(index);
                    selectedRecipient = clickedPhysician;
                    selectedRecipientLabel.setText(UIConfig.SELECTED_PREFIX + selectedRecipient.getName());
                    refreshMessages();
                }
            }
        });

        JScrollPane searchScrollPane = new JScrollPane(searchResultsList);
        searchScrollPane.setPreferredSize(new Dimension(400, 150));
        searchScrollPane.setBorder(BorderFactory.createTitledBorder(UIConfig.ALL_PHYSICIANS_BORDER));

        // ─────────── Message List ───────────
        messageListModel = new DefaultListModel<>();
        messageList      = new JList<>(messageListModel);
        messageList.setCellRenderer(new MessageCellRenderer());
        messageList.setFont(UITheme.LABEL_FONT);
        messageList.setBackground(UITheme.BACKGROUND_COLOR);
        messageList.setFixedCellHeight(60);
        JScrollPane messageScrollPane = new JScrollPane(messageList);
        messageScrollPane.setPreferredSize(new Dimension(400, 300));
        messageScrollPane.setBorder(BorderFactory.createTitledBorder(UIConfig.MESSAGES_BORDER));

        // ─────────── Input Panel ───────────
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(UITheme.BACKGROUND_COLOR);

        messageInput = new JTextField();
        messageInput.setFont(UITheme.LABEL_FONT);
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        JButton sendButton = new JButton(UIConfig.SEND_BUTTON_TEXT);
        sendButton.setFont(UITheme.BUTTON_FONT);
        sendButton.setBackground(UITheme.PRIMARY_COLOR);
        sendButton.setForeground(UITheme.BACKGROUND_COLOR);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(true);
        UITheme.applyHoverEffect(sendButton);
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // ─────────── Assemble Left and Center Containers ───────────
        JPanel searchContainer = new JPanel(new BorderLayout(5, 5));
        searchContainer.setBackground(UITheme.BACKGROUND_COLOR);
        searchContainer.add(searchPanel, BorderLayout.NORTH);
        searchContainer.add(searchScrollPane, BorderLayout.CENTER);
        searchContainer.add(selectedRecipientLabel, BorderLayout.SOUTH);

        JPanel messageContainer = new JPanel(new BorderLayout(5, 5));
        messageContainer.setBackground(UITheme.BACKGROUND_COLOR);
        messageContainer.add(messageScrollPane, BorderLayout.CENTER);
        messageContainer.add(inputPanel, BorderLayout.SOUTH);

        // ─────────── Add Components to Main Panel ───────────
        add(headerPanel, BorderLayout.NORTH);
        add(searchContainer, BorderLayout.WEST);
        add(messageContainer, BorderLayout.CENTER);

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
                    .filter(p -> p.getName().toLowerCase().contains(searchText.toLowerCase())
                            || p.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(searchResultsModel::addElement);
        }
    }

    private void refreshMessages() {
        messageListModel.clear();
        if (selectedRecipient != null) {
            List<Message> messages = messageController.getAllMessagesForUser(currentUserId);

            List<Message> conversationMessages = messages.stream()
                    .filter(m -> m.getSenderId().equals(selectedRecipient.getId())
                            || m.getReceiverId().equals(selectedRecipient.getId()))
                    .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                    .toList();

            conversationMessages.forEach(messageListModel::addElement);

            // Mark incoming ones as read
            conversationMessages.stream()
                    .filter(m -> m.getReceiverId().equals(currentUserId)
                            && m.getSenderId().equals(selectedRecipient.getId())
                            && !m.isRead())
                    .forEach(m -> {
                        messageController.markMessageAsRead(m.getMessageId());
                        m.setRead(true);
                    });

            scrollToBottom();
        }
        updateUnreadCount();
    }

    private void updateUnreadCount() {
        int unreadCount = messageController.getUnreadMessageCount(currentUserId);
        unreadCountLabel.setText(unreadCount > 0
                ? unreadCount + " " + UIConfig.UNREAD_SUFFIX
                : "");
        showAllPhysicians();
    }

    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (!content.isEmpty() && selectedRecipient != null) {
            try {
                Message sentMessage = messageController.sendMessage(
                        currentUserId,
                        selectedRecipient.getId(),
                        content
                );
                messageInput.setText("");
                messageListModel.addElement(sentMessage);
                scrollToBottom();
                updateUnreadCount();
            } catch (InvalidMessageException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        UIConfig.ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else if (selectedRecipient == null) {
            JOptionPane.showMessageDialog(
                    this,
                    UIConfig.ERROR_NO_RECIPIENT,
                    UIConfig.ERROR_DIALOG_TITLE,
                    JOptionPane.WARNING_MESSAGE
            );
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

    private class MessageCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Message) {
                Message message = (Message) value;
                boolean isSent = message.getSenderId().equals(currentUserId);

                String timestamp = message.getTimestamp().format(TIME_FORMATTER);
                String status = isSent
                        ? (message.isRead() ? UIConfig.STATUS_READ : UIConfig.STATUS_SENT)
                        : "";

                setText(String.format(
                        "<html><div style='width: 100%%; padding: 5px;'>" +
                                "<b>%s</b> (%s) %s<br>%s</div></html>",
                        isSent ? UIConfig.YOU_LABEL : selectedRecipient.getName(),
                        timestamp,
                        status,
                        message.getContent()
                ));

                setHorizontalAlignment(isSent
                        ? SwingConstants.RIGHT
                        : SwingConstants.LEFT);

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (!isSelected) {
                    setBackground(isSent
                            ? UITheme.POSITIVE_COLOR
                            : UITheme.BACKGROUND_COLOR);
                }
                setPreferredSize(new Dimension(list.getWidth() - 20, getPreferredSize().height));
            }

            return this;
        }
    }
}