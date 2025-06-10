package physicianconnect.presentation;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class NotificationButtonTest {

    @Test
    void testInitialState() {
        NotificationButton btn = new NotificationButton();
        JButton button = (JButton) getField(btn, "notificationButton");
        JLabel label = (JLabel) getField(btn, "notificationLabel");

        assertEquals("Alerts", button.getText());
        assertFalse(label.isVisible());
    }

    @Test
    void testUpdateNotificationCount() {
        NotificationButton btn = new NotificationButton();
        JLabel label = (JLabel) getField(btn, "notificationLabel");

        btn.updateNotificationCount(0);
        assertFalse(label.isVisible());

        btn.updateNotificationCount(5);
        assertTrue(label.isVisible());
        assertEquals("5", label.getText());

        btn.updateNotificationCount(0);
        assertFalse(label.isVisible());
    }

    @Test
    void testSetOnAction() {
        NotificationButton btn = new NotificationButton();
        JButton button = (JButton) getField(btn, "notificationButton");

        final boolean[] called = {false};
        btn.setOnAction(e -> called[0] = true);

        for (ActionListener l : button.getActionListeners()) {
            l.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "Alerts"));
        }
        assertTrue(called[0]);
    }

    // Helper to access private fields
    private Object getField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}