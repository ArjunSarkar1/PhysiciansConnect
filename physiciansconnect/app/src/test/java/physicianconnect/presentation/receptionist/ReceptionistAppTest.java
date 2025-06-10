package physicianconnect.presentation.receptionist;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import physicianconnect.logic.manager.*;
import physicianconnect.logic.controller.*;
import physicianconnect.logic.*;
import physicianconnect.objects.*;
import physicianconnect.persistence.PersistenceFactory;
import physicianconnect.persistence.interfaces.*;
import physicianconnect.persistence.sqlite.AppointmentDB;
import physicianconnect.presentation.DailyAvailabilityPanel;
import physicianconnect.presentation.WeeklyAvailabilityPanel;
import physicianconnect.presentation.config.UIConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ReceptionistAppTest {
    Receptionist loggedIn;
    PhysicianManager physicianManager;
    AppointmentManager appointmentManager;
    ReceptionistManager receptionistManager;
    AppointmentController appointmentController;
    Runnable logoutCallback;

    // Persistence/DB mocks
    MockedStatic<PersistenceFactory> persistenceFactoryMock;
    InvoicePersistence invoicePersistenceMock;
    PaymentPersistence paymentPersistenceMock;
    MessageRepository messageRepositoryMock;
    NotificationPersistence notificationPersistenceMock;
    AppointmentDB appointmentDBMock;
    PhysicianPersistence physicianPersistenceMock;
    ReceptionistPersistence receptionistPersistenceMock;
    MedicationPersistence medicationPersistenceMock;
    PrescriptionPersistence prescriptionPersistenceMock;
    ReferralPersistence referralPersistenceMock;

    @BeforeEach
    void setup() {
        loggedIn = new Receptionist("r1", "Receptionist", "r@email.com", "pw");
        physicianManager = mock(PhysicianManager.class);
        appointmentManager = mock(AppointmentManager.class);
        receptionistManager = mock(ReceptionistManager.class);
        appointmentController = mock(AppointmentController.class);
        logoutCallback = mock(Runnable.class);

        // Mock managers' methods
        when(physicianManager.getAllPhysicians()).thenReturn(List.of(
                new Physician("doc1", "Dr. Banner", "b@a.com", "pw"),
                new Physician("doc2", "Dr. Stark", "s@a.com", "pw")));
        when(physicianManager.getPhysicianById("doc1"))
                .thenReturn(new Physician("doc1", "Dr. Banner", "b@a.com", "pw"));
        when(physicianManager.getPhysicianById("doc2")).thenReturn(new Physician("doc2", "Dr. Stark", "s@a.com", "pw"));
        when(receptionistManager.getAllReceptionists()).thenReturn(List.of(loggedIn));
        when(receptionistManager.getReceptionistById("r1")).thenReturn(loggedIn);

        // Mock all persistence/db
        invoicePersistenceMock = mock(InvoicePersistence.class);
        paymentPersistenceMock = mock(PaymentPersistence.class);
        messageRepositoryMock = mock(MessageRepository.class);
        notificationPersistenceMock = mock(NotificationPersistence.class);
        appointmentDBMock = mock(AppointmentDB.class);
        physicianPersistenceMock = mock(PhysicianPersistence.class);
        receptionistPersistenceMock = mock(ReceptionistPersistence.class);
        medicationPersistenceMock = mock(MedicationPersistence.class);
        prescriptionPersistenceMock = mock(PrescriptionPersistence.class);
        referralPersistenceMock = mock(ReferralPersistence.class);

        // Mock all methods that could be called
        when(invoicePersistenceMock.getAllInvoices()).thenReturn(List.of());
        when(paymentPersistenceMock.getPaymentsByInvoice(anyString())).thenReturn(List.of());
        when(paymentPersistenceMock.getPaymentsByMonth(anyInt(), anyInt())).thenReturn(List.of());
        when(notificationPersistenceMock.getNotificationsForUser(anyString(), anyString())).thenReturn(List.of());
        when(messageRepositoryMock.findByReceiverId(anyString(), anyString())).thenReturn(List.of());
        when(messageRepositoryMock.findBySenderId(anyString(), anyString())).thenReturn(List.of());
        when(messageRepositoryMock.findUnreadByReceiverId(anyString(), anyString())).thenReturn(List.of());
        when(messageRepositoryMock.countUnreadMessages(anyString(), anyString())).thenReturn(0);
        when(appointmentDBMock.getAppointmentsForPhysician(anyString())).thenReturn(List.of());
        when(physicianPersistenceMock.getAllPhysicians()).thenReturn(List.of());
        when(receptionistPersistenceMock.getAllReceptionists()).thenReturn(List.of());
        when(medicationPersistenceMock.getAllMedications()).thenReturn(List.of());
        when(prescriptionPersistenceMock.getAllPrescriptions()).thenReturn(List.of());

        // Mock static PersistenceFactory to return all mocks
        persistenceFactoryMock = mockStatic(PersistenceFactory.class);
        persistenceFactoryMock.when(PersistenceFactory::getInvoicePersistence).thenReturn(invoicePersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getPaymentPersistence).thenReturn(paymentPersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getMessageRepository).thenReturn(messageRepositoryMock);
        persistenceFactoryMock.when(PersistenceFactory::getNotificationPersistence)
                .thenReturn(notificationPersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getAppointmentPersistence).thenReturn(appointmentDBMock);
        persistenceFactoryMock.when(PersistenceFactory::getPhysicianPersistence).thenReturn(physicianPersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getReceptionistPersistence)
                .thenReturn(receptionistPersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getMedicationPersistence).thenReturn(medicationPersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getPrescriptionPersistence)
                .thenReturn(prescriptionPersistenceMock);
        persistenceFactoryMock.when(PersistenceFactory::getReferralPersistence).thenReturn(referralPersistenceMock);
    }

    @AfterEach
    void tearDown() {
        if (persistenceFactoryMock != null) {
            persistenceFactoryMock.close();
        }
    }

    // --- Reflection helpers for private fields/components ---
    private Object getField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JButton getButtonByText(JFrame frame, String text) {
        return findButton(frame.getContentPane(), text);
    }

    private JButton findButton(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton btn && btn.getText() != null && btn.getText().equals(text)) {
                return btn;
            }
            if (comp instanceof Container child) {
                JButton found = findButton(child, text);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    @Test
    void testConstructorInitializesUI() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        assertNotNull(frame);
        assertTrue(frame.isVisible());
        assertTrue(frame.getTitle().contains(loggedIn.getName()));
    }

    @Test
    void testSignOutButtonCallsLogoutCallback() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton signOutButton = getButtonByText(frame, UIConfig.LOGOUT_BUTTON_TEXT);
        assertNotNull(signOutButton);
        signOutButton.doClick();
        verify(logoutCallback, atLeastOnce()).run();
    }

    @Test
    void testAddAppointmentButtonShowsDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton addButton = getButtonByText(frame, UIConfig.ADD_APPOINTMENT_BUTTON_TEXT);
        assertNotNull(addButton);
        JComboBox<?> combo = (JComboBox<?>) getField(app, "physicianCombo");
        combo.setSelectedIndex(1); // select first physician
        addButton.doClick();
    }

    @Test
    void testViewAppointmentButtonShowsDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton viewButton = getButtonByText(frame, UIConfig.VIEW_APPOINTMENTS_BUTTON_TEXT);
        assertNotNull(viewButton);
    }

    @Test
    void testBillingButtonShowsDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton billingButton = getButtonByText(frame, UIConfig.BILLING_BUTTON_TEXT);
        assertNotNull(billingButton);
        billingButton.doClick();
    }

    @Test
    void testProfilePicButtonOpensProfileDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JButton profilePicButton = (JButton) getField(app, "profilePicButton");
        assertNotNull(profilePicButton);
        profilePicButton.doClick();
    }

    @Test
    void testAppointmentChangeHandlers() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        Appointment appt = new Appointment("doc1", "Bruce Banner", LocalDateTime.now().plusMinutes(5));
        app.onAppointmentCreated(appt);
        app.onAppointmentUpdated(appt);
        app.onAppointmentDeleted(appt);
    }

    @Test
    void testFilterAppointmentsUpdatesTable() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JTextField searchField = (JTextField) getField(app, "appointmentSearchField");
        DefaultTableModel model = (DefaultTableModel) getField(app, "appointmentTableModel");
        JTable table = (JTable) getField(app, "appointmentTable");
        model.addRow(new Object[] { "Bruce Banner", "Dr. Banner", "2025-06-10", "10:00" });
        searchField.setText("Bruce");
        assertTrue(table.getRowCount() > 0);
    }

    // --- Additional coverage tests ---

    @Test
    void testShowNotificationPanelDoesNotCrash() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        try {
            var m = app.getClass().getDeclaredMethod("showNotificationPanel");
            m.setAccessible(true);
            m.invoke(app);
        } catch (Exception e) {
            fail("showNotificationPanel threw: " + e.getMessage());
        }
    }

    @Test
    void testShowMessageDialogDoesNotCrash() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        try {
            var m = app.getClass().getDeclaredMethod("showMessageDialog");
            m.setAccessible(true);
            m.invoke(app);
        } catch (Exception e) {
            fail("showMessageDialog threw: " + e.getMessage());
        }
    }

    @Test
    void testRefreshMessageCountDoesNotCrash() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        try {
            var m = app.getClass().getDeclaredMethod("refreshMessageCount");
            m.setAccessible(true);
            m.invoke(app);
        } catch (Exception e) {
            fail("refreshMessageCount threw: " + e.getMessage());
        }
    }

    @Test
    void testRefreshNotificationCountDoesNotCrash() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        try {
            var m = app.getClass().getDeclaredMethod("refreshNotificationCount");
            m.setAccessible(true);
            m.invoke(app);
        } catch (Exception e) {
            fail("refreshNotificationCount threw: " + e.getMessage());
        }
    }

    @Test
    void testNotifyAppointmentChangeDoesNotCrash() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        try {
            var m = app.getClass().getDeclaredMethod("notifyAppointmentChange", String.class, String.class);
            m.setAccessible(true);
            m.invoke(app, "Test message", "Test type");
        } catch (Exception e) {
            fail("notifyAppointmentChange threw: " + e.getMessage());
        }
    }

    @Test
    void testRevenueHeaderCollapseExpand() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JPanel revenueSummaryPanel = (JPanel) getField(app, "revenueSummaryPanel");
        JPanel revenueSummaryContent = (JPanel) getField(app, "revenueSummaryContent");
        boolean revenueSummaryCollapsed = (boolean) getField(app, "revenueSummaryCollapsed");

        // Find the revenue header button
        JButton revenueHeader = null;
        for (Component c : revenueSummaryPanel.getComponents()) {
            if (c instanceof JButton btn) {
                revenueHeader = btn;
                break;
            }
        }
        assertNotNull(revenueHeader);

        // Click to collapse
        revenueHeader.doClick();
        assertTrue(revenueSummaryContent.isVisible() == !((boolean) getField(app, "revenueSummaryCollapsed")));

        // Click to expand
        revenueHeader.doClick();
        assertTrue(revenueSummaryContent.isVisible() == !((boolean) getField(app, "revenueSummaryCollapsed")));
    }

    @Test
    void testPrevNextDayButtons() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JPanel dayNav = (JPanel) getField(app, "dayNav");
        JLabel dayLabel = (JLabel) getField(app, "dayLabel");
        DailyAvailabilityPanel dailyPanel = (DailyAvailabilityPanel) getField(app, "dailyPanel");
        LocalDate originalDate = (LocalDate) getField(app, "selectedDate");

        JButton prevDayBtn = null, nextDayBtn = null;
        for (Component c : dayNav.getComponents()) {
            if (c instanceof JButton btn) {
                if (btn.getText().equals(UIConfig.PREV_DAY_BUTTON_TEXT))
                    prevDayBtn = btn;
                if (btn.getText().equals(UIConfig.NEXT_DAY_BUTTON_TEXT))
                    nextDayBtn = btn;
            }
        }
        assertNotNull(prevDayBtn);
        assertNotNull(nextDayBtn);

        prevDayBtn.doClick();
        LocalDate afterPrev = (LocalDate) getField(app, "selectedDate");
        assertEquals(originalDate.minusDays(1), afterPrev);

        nextDayBtn.doClick();
        LocalDate afterNext = (LocalDate) getField(app, "selectedDate");
        assertEquals(originalDate, afterNext);
    }

    @Test
    void testPrevNextWeekButtons() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JPanel weekNav = (JPanel) getField(app, "weekNav");
        JLabel weekLabel = (JLabel) getField(app, "weekLabel");
        WeeklyAvailabilityPanel weeklyPanel = (WeeklyAvailabilityPanel) getField(app, "weeklyPanel");
        LocalDate originalWeek = (LocalDate) getField(app, "weekStart");

        JButton prevWeekBtn = null, nextWeekBtn = null;
        for (Component c : weekNav.getComponents()) {
            if (c instanceof JButton btn) {
                if (btn.getText().equals(UIConfig.PREV_WEEK_BUTTON_TEXT))
                    prevWeekBtn = btn;
                if (btn.getText().equals(UIConfig.NEXT_WEEK_BUTTON_TEXT))
                    nextWeekBtn = btn;
            }
        }
        assertNotNull(prevWeekBtn);
        assertNotNull(nextWeekBtn);

        prevWeekBtn.doClick();
        LocalDate afterPrev = (LocalDate) getField(app, "weekStart");
        assertEquals(originalWeek.minusWeeks(1), afterPrev);

        nextWeekBtn.doClick();
        LocalDate afterNext = (LocalDate) getField(app, "weekStart");
        assertEquals(originalWeek, afterNext);
    }

    @Test
    void testAddAppointmentButtonNoPhysicianSelectedShowsDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton addButton = getButtonByText(frame, UIConfig.ADD_APPOINTMENT_BUTTON_TEXT);
        JComboBox<?> combo = (JComboBox<?>) getField(app, "physicianCombo");
        combo.setSelectedIndex(0); // "All Physicians" (not a Physician)
        // This should show a warning dialog (simulate, won't throw)
        addButton.doClick();
    }

    @Test
    void testViewAppointmentButtonNoSelectionShowsDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton viewButton = getButtonByText(frame, UIConfig.VIEW_APPOINTMENTS_BUTTON_TEXT);
        JTable table = (JTable) getField(app, "appointmentTable");
        table.clearSelection(); // No row selected
        viewButton.doClick(); // Should show info dialog, not throw
    }

    @Test
    void testViewAppointmentButtonNotFoundShowsDialog() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JFrame frame = (JFrame) getField(app, "frame");
        JButton viewButton = getButtonByText(frame, UIConfig.VIEW_APPOINTMENTS_BUTTON_TEXT);
        JTable table = (JTable) getField(app, "appointmentTable");
        DefaultTableModel model = (DefaultTableModel) getField(app, "appointmentTableModel");
        // Add a row that won't match any appointment
        model.addRow(new Object[] { "Ghost", "Dr. Who", "2099-01-01", "00:00" });
        table.setRowSelectionInterval(0, 0);
        viewButton.doClick(); // Should show error dialog, not throw
    }

    @Test
    void testFilterAppointmentsEmptyAndNonEmpty() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        JTextField searchField = (JTextField) getField(app, "appointmentSearchField");
        DefaultTableModel model = (DefaultTableModel) getField(app, "appointmentTableModel");
        JTable table = (JTable) getField(app, "appointmentTable");
        model.addRow(new Object[] { "Bruce Banner", "Dr. Banner", "2025-06-10", "10:00" });

        // No filter
        searchField.setText("");
        assertTrue(table.getRowCount() > 0);

        // Filter with text
        searchField.setText("Bruce");
        assertTrue(table.getRowCount() > 0);
    }

    @Test
    void testShowNotificationPanelCreatesDialogIfNull() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        // Set notificationDialog to null to force creation
        try {
            Field f = app.getClass().getDeclaredField("notificationDialog");
            f.setAccessible(true);
            f.set(app, null);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
        try {
            Method m = app.getClass().getDeclaredMethod("showNotificationPanel");
            m.setAccessible(true);
            m.invoke(app);
        } catch (Exception e) {
            fail("showNotificationPanel threw: " + e.getMessage());
        }
    }

    @Test
    void testRefreshMessageCountWithNewUnreadMessage() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        // Simulate new unread message
        MessageService messageService = (MessageService) getField(app, "messageService");
        ReceptionistAppTest testInstance = this;
        // Use a spy to override getUnreadMessageCount and getUnreadMessagesForUser
        MessageService spyService = spy(messageService);
        doReturn(2).when(spyService).getUnreadMessageCount(anyString(), anyString());
        physicianconnect.objects.Message mockMsg = mock(physicianconnect.objects.Message.class);
        when(mockMsg.getSenderType()).thenReturn("physician");
        when(mockMsg.getSenderId()).thenReturn("doc1");
        doReturn(List.of(mockMsg, mockMsg)).when(spyService).getUnreadMessagesForUser(anyString(), anyString());
        try {
            Field f = app.getClass().getDeclaredField("messageService");
            f.setAccessible(true);
            f.set(app, spyService);
            Method m = app.getClass().getDeclaredMethod("refreshMessageCount");
            m.setAccessible(true);
            m.invoke(app);
        } catch (Exception e) {
            fail("refreshMessageCount threw: " + e.getMessage());
        }
    }

    @Test
    void testNotifyAppointmentChangeCreatesPanelIfNull() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        // Set notificationPanel to null to force creation
        try {
            Field f = app.getClass().getDeclaredField("notificationPanel");
            f.setAccessible(true);
            f.set(app, null);
            Method m = app.getClass().getDeclaredMethod("notifyAppointmentChange", String.class, String.class);
            m.setAccessible(true);
            m.invoke(app, "Test message", "Test type");
        } catch (Exception e) {
            fail("notifyAppointmentChange threw: " + e.getMessage());
        }
    }

    @Test
    void testProfileDialogUpdateReceptionistInfo() {
        ReceptionistApp app = new ReceptionistApp(
                loggedIn, physicianManager, appointmentManager, receptionistManager, appointmentController,
                logoutCallback);
        // Open profile dialog and simulate update callback
        try {
            Method m = app.getClass().getDeclaredMethod("openProfileDialog");
            m.setAccessible(true);
            m.invoke(app);
            // Simulate update callback
            Receptionist refreshed = new Receptionist("r1", "Receptionist Updated", "r@email.com", "pw");
            when(receptionistManager.getReceptionistById("r1")).thenReturn(refreshed);
            // Call the update lambda (simulate by reopening dialog)
            m.invoke(app);
        } catch (Exception e) {
            fail("openProfileDialog threw: " + e.getMessage());
        }
    }
}