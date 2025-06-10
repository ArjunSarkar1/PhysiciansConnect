package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.objects.Notification;
import physicianconnect.persistence.interfaces.NotificationPersistence;

import javax.swing.*;
import java.awt.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationPanelTest {

    @Mock
    NotificationPersistence notificationPersistence;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadNotificationsFromPersistence() {
        List<Notification> stored = new ArrayList<>();
        stored.add(new Notification("msg1", "type1", LocalDateTime.now().minusMinutes(10), "uid", "utype"));
        stored.add(new Notification("msg2", "type2", LocalDateTime.now().minusMinutes(5), "uid", "utype"));
        when(notificationPersistence.getNotificationsForUser("uid", "utype")).thenReturn(stored);

        NotificationPanel panel = new NotificationPanel(notificationPersistence, "uid", "utype");
        panel.loadNotifications();

        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "notificationListModel");
        assertEquals(2, model.size());
        assertEquals("msg1", ((Notification) model.get(1)).getMessage());
        assertEquals("msg2", ((Notification) model.get(0)).getMessage());
    }

    @Test
    void testAddNotificationAddsToPersistenceAndUnread() {
        NotificationPanel panel = new NotificationPanel(notificationPersistence, "uid", "utype");
        panel.addNotification("new message", "typeX");

        // Should call persistence
        verify(notificationPersistence).addNotification(any(Notification.class));

        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "notificationListModel");
        assertEquals(1, model.size());
        Notification n = (Notification) model.get(0);
        assertEquals("new message", n.getMessage());
        assertEquals("typeX", n.getType());

        // Should be in unread
        List<?> unread = (List<?>) getField(panel, "unreadNotifications");
        assertTrue(unread.contains(n));
    }

    @Test
    void testMaxNotificationsEnforced() {
        NotificationPanel panel = new NotificationPanel(notificationPersistence, "uid", "utype");
        // Add 12 notifications (MAX_NOTIFICATIONS is 10)
        for (int i = 0; i < 12; i++) {
            panel.addNotification("msg" + i, "type");
        }
        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "notificationListModel");
        assertEquals(10, model.size());
        // The most recent is at index 0
        assertEquals("msg11", ((Notification) model.get(0)).getMessage());
        assertEquals("msg2", ((Notification) model.get(9)).getMessage());
    }

    @Test
    void testUnreadNotificationCount() {
        NotificationPanel panel = new NotificationPanel(notificationPersistence, "uid", "utype");
        assertEquals(0, panel.getUnreadNotificationCount());
        panel.addNotification("msg", "type");
        assertEquals(1, panel.getUnreadNotificationCount());
    }

    @Test
    void testMarkAllAsReadClearsUnread() {
        NotificationPanel panel = new NotificationPanel(notificationPersistence, "uid", "utype");
        panel.addNotification("msg", "type");
        assertEquals(1, panel.getUnreadNotificationCount());
        panel.markAllAsRead();
        assertEquals(0, panel.getUnreadNotificationCount());
        // The notification should be marked as read
        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "notificationListModel");
        Notification n = (Notification) model.get(0);
        assertTrue(n.isRead());
    }

    @Test
    void testCellRendererSetsTextAndColors() {
        NotificationPanel panel = new NotificationPanel(notificationPersistence, "uid", "utype");
        addNotification(panel, "msg", "Appointment Cancellation!", LocalDateTime.now().minusMinutes(1));
        DefaultListModel<?> model = (DefaultListModel<?>) getField(panel, "notificationListModel");
        Notification n = (Notification) model.get(0);

        JList<?> notificationList = (JList<?>) getField(panel, "notificationList");
        ListCellRenderer<? super Notification> renderer = (ListCellRenderer<? super Notification>) notificationList.getCellRenderer();
        Component comp = renderer.getListCellRendererComponent(new JList<>(), n, 0, true, true);
        assertTrue(comp instanceof JLabel);
        String html = ((JLabel) comp).getText();
        assertTrue(html.contains("Appointment Cancellation!"));
        assertTrue(html.contains("msg"));
    }

    // Helper to add notification with custom time
    private void addNotification(NotificationPanel panel, String msg, String type, LocalDateTime time) {
        Notification n = new Notification(msg, type, time, "uid", "utype");
        DefaultListModel<Notification> model = (DefaultListModel<Notification>) getField(panel, "notificationListModel");
        model.add(0, n);
        List<Notification> unread = (List<Notification>) getField(panel, "unreadNotifications");
        unread.add(n);
    }

    // Reflection helper
    private Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}