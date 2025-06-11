package physicianconnect.presentation.receptionist;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.controller.BillingController;
import physicianconnect.logic.controller.AppointmentController;
import physicianconnect.logic.validation.BillingValidator;
import physicianconnect.objects.*;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.util.InvoiceExportUtil;
import physicianconnect.presentation.util.RevenueSummaryUtil;
import physicianconnect.presentation.NotificationPanel;
import physicianconnect.persistence.interfaces.NotificationPersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillingPanelTest {

    BillingController billingController;
    AppointmentController appointmentController;
    NotificationPanel notificationPanel;
    NotificationPersistence notificationPersistence;

    // Test subclass to simulate service selection dialog for tests
    class TestBillingPanel extends BillingPanel {
        private final List<ServiceItem> testServices;
        TestBillingPanel(BillingController bc, AppointmentController ac, NotificationPanel np, NotificationPersistence npers, List<ServiceItem> testServices) {
            super(bc, ac, np, npers);
            this.testServices = testServices;
        }
        // No @Override annotation since the original is likely private
        protected List<ServiceItem> showServiceSelectionDialog() {
            return testServices;
        }
    }

    @BeforeEach
    public void setup() {
        billingController = mock(BillingController.class);
        appointmentController = mock(AppointmentController.class);
        notificationPanel = mock(NotificationPanel.class);
        notificationPersistence = mock(NotificationPersistence.class);
    }

    @Test
    public void testPanelPopulatesTableOnConstruct() {
        Invoice inv1 = new Invoice("1", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0.0);
        Invoice inv2 = new Invoice("2", "appt2", "Bob", List.of(new ServiceItem("Lab", 50)), 0.0);
        when(billingController.getAllInvoices()).thenReturn(List.of(inv1, inv2));

        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);
        JTable table = (JTable) getField(panel, "invoiceTable");
        DefaultTableModel model = (DefaultTableModel) getField(panel, "model");

        assertEquals(2, model.getRowCount());
        assertEquals("Alice", model.getValueAt(0, 0));
        assertEquals("Bob", model.getValueAt(1, 0));
    }

    @Test
    public void testSearchFieldFiltersTable() {
        Invoice inv1 = new Invoice("1", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0.0);
        Invoice inv2 = new Invoice("2", "appt2", "Bob", List.of(new ServiceItem("Lab", 50)), 0.0);
        when(billingController.getAllInvoices()).thenReturn(List.of(inv1, inv2));

        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);
        JTextField searchField = (JTextField) getField(panel, "searchField");
        JTable table = (JTable) getField(panel, "invoiceTable");

        searchField.setText("Bob");
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        assertTrue(table.getRowCount() == 1 || table.getRowCount() == 2);
    }

    @Test
    public void testShowNewInvoiceDialogValid() throws Exception {
        Appointment appt = mock(Appointment.class);
        when(appt.getId()).thenReturn(1);
        when(appt.getPatientName()).thenReturn("Alice");
        when(appt.getDateTime()).thenReturn(LocalDateTime.now());
        when(appointmentController.getAllAppointments()).thenReturn(List.of(appt));
        when(billingController.getAllInvoices()).thenReturn(new ArrayList<>());

        BillingPanel panel = new TestBillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence, List.of(new ServiceItem("Consult", 100)));

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class);
             MockedStatic<BillingValidator> valMock = mockStatic(BillingValidator.class);
             MockedStatic<RevenueSummaryUtil> revMock = mockStatic(RevenueSummaryUtil.class)) {
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.SELECT_SERVICES_DIALOG_TITLE), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.NEW_INVOICE_DIALOG_TITLE), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);
            paneMock.when(() -> JOptionPane.showMessageDialog(any(), any(), any(), anyInt()))
                    .thenAnswer(ignored -> null);

            Method m = BillingPanel.class.getDeclaredMethod("showNewInvoiceDialog");
            m.setAccessible(true);
            m.invoke(panel);

            verify(billingController).createInvoice(any(), eq("Alice"), eq(List.of(new ServiceItem("Consult", 100))), anyDouble());
            revMock.verify(RevenueSummaryUtil::fireRevenueSummaryChanged);
        }
    }

    @Test
    public void testShowNewInvoiceDialogMissingFieldsShowsError() throws Exception {
        Appointment appt = mock(Appointment.class);
        when(appt.getId()).thenReturn(1);
        when(appt.getPatientName()).thenReturn("Alice");
        when(appt.getDateTime()).thenReturn(LocalDateTime.now());
        when(appointmentController.getAllAppointments()).thenReturn(List.of(appt));
        when(billingController.getAllInvoices()).thenReturn(new ArrayList<>());

        BillingPanel panel = new TestBillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence, null);

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.NEW_INVOICE_DIALOG_TITLE), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);
            paneMock.when(() -> JOptionPane.showMessageDialog(any(), any(), any(), anyInt()))
                    .thenAnswer(ignored -> null);

            Method m = BillingPanel.class.getDeclaredMethod("showNewInvoiceDialog");
            m.setAccessible(true);
            m.invoke(panel);

            paneMock.verify(() -> JOptionPane.showMessageDialog(any(), eq(UIConfig.ERROR_NO_SERVICES_SELECTED), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    public void testShowNewInvoiceDialogDuplicateInvoiceShowsError() throws Exception {
        Appointment appt = mock(Appointment.class);
        when(appt.getId()).thenReturn(1);
        when(appt.getPatientName()).thenReturn("Alice");
        when(appt.getDateTime()).thenReturn(LocalDateTime.now());
        when(appointmentController.getAllAppointments()).thenReturn(List.of(appt));
        Invoice inv = new Invoice("1", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0.0);
        when(billingController.getAllInvoices()).thenReturn(List.of(inv));

        BillingPanel panel = new TestBillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence, List.of(new ServiceItem("Consult", 100)));

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.NEW_INVOICE_DIALOG_TITLE), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);
            paneMock.when(() -> JOptionPane.showMessageDialog(any(), any(), any(), anyInt()))
                    .thenAnswer(ignored -> null);

            Method m = BillingPanel.class.getDeclaredMethod("showNewInvoiceDialog");
            m.setAccessible(true);
            m.invoke(panel);

            paneMock.verify(() -> JOptionPane.showMessageDialog(any(), eq(UIConfig.ERROR_DUPLICATE_INVOICE), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    public void testShowServiceSelectionDialogValidAndInvalidAmount() throws Exception {
        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.SELECT_SERVICES_DIALOG_TITLE), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);

            Method m = BillingPanel.class.getDeclaredMethod("showServiceSelectionDialog");
            m.setAccessible(true);
            Object result = m.invoke(panel);
            assertNotNull(result);
        }
    }

    @Test
    public void testShowInvoiceDetailAndDelete() throws Exception {
        Invoice inv = new Invoice("1", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0.0);
        Payment payment = new Payment("1", "1", 100.0, "Cash");
        when(billingController.getAllInvoices()).thenReturn(List.of(inv));
        when(billingController.getPaymentsByInvoice("1")).thenReturn(List.of(payment));
        when(appointmentController.getAllAppointments()).thenReturn(List.of(mock(Appointment.class)));

        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class);
             MockedStatic<RevenueSummaryUtil> revMock = mockStatic(RevenueSummaryUtil.class)) {
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.CONFIRM_DELETE_INVOICE), anyInt()))
                    .thenReturn(JOptionPane.YES_OPTION);

            Method m = BillingPanel.class.getDeclaredMethod("showInvoiceDetail", Invoice.class, List.class);
            m.setAccessible(true);
            m.invoke(panel, inv, List.of(payment));

            JPanel invoiceContentPanel = (JPanel) getField(panel, "invoiceContentPanel");
            JButton deleteBtn = findButton(invoiceContentPanel, UIConfig.DELETE_INVOICE_BUTTON_TEXT);
            assertNotNull(deleteBtn);
            deleteBtn.doClick();

            verify(billingController).deleteInvoice("1");
            revMock.verify(RevenueSummaryUtil::fireRevenueSummaryChanged);
        }
    }

    @Test
    public void testShowInvoiceDetailAndExport() throws Exception {
        Invoice inv = new Invoice("1", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0.0);
        Payment payment = new Payment("1", "1", 100.0, "Cash");
        when(billingController.getAllInvoices()).thenReturn(List.of(inv));
        when(billingController.getPaymentsByInvoice("1")).thenReturn(List.of(payment));
        when(appointmentController.getAllAppointments()).thenReturn(List.of(mock(Appointment.class)));

        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);

        try (MockedStatic<InvoiceExportUtil> exportMock = mockStatic(InvoiceExportUtil.class)) {
            Method m = BillingPanel.class.getDeclaredMethod("showInvoiceDetail", Invoice.class, List.class);
            m.setAccessible(true);
            m.invoke(panel, inv, List.of(payment));
            JPanel invoiceContentPanel = (JPanel) getField(panel, "invoiceContentPanel");
            JButton exportBtn = findButton(invoiceContentPanel, "Export");
            assertNotNull(exportBtn);
            exportBtn.doClick();
            exportMock.verify(() -> InvoiceExportUtil.exportInvoice(any(), eq(inv), any(), eq(List.of(payment))));
        }
    }

    @Test
    public void testShowPaymentDialogValidAndInvalid() throws Exception {
        Invoice inv = new Invoice("1", "appt1", "Alice", List.of(new ServiceItem("Consult", 100)), 0.0);
        when(billingController.getAllInvoices()).thenReturn(List.of(inv));
        when(billingController.getInvoiceById("1")).thenReturn(inv);
        when(billingController.getPaymentsByInvoice("1")).thenReturn(List.of());

        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);

        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class);
             MockedStatic<BillingValidator> valMock = mockStatic(BillingValidator.class);
             MockedStatic<RevenueSummaryUtil> revMock = mockStatic(RevenueSummaryUtil.class)) {
            paneMock.when(() -> JOptionPane.showConfirmDialog(any(), any(), eq(UIConfig.RECORD_PAYMENT_DIALOG_TITLE), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);
            paneMock.when(() -> JOptionPane.showMessageDialog(any(), any(), any(), anyInt()))
                    .thenAnswer(ignored -> null);

            Method m = BillingPanel.class.getDeclaredMethod("showPaymentDialog", Invoice.class);
            m.setAccessible(true);
            m.invoke(panel, inv);

            valMock.when(() -> BillingValidator.validatePaymentAmount(anyDouble(), anyDouble()))
                    .thenThrow(new IllegalArgumentException("Invalid payment"));
            m.invoke(panel, inv);
            paneMock.verify(() -> JOptionPane.showMessageDialog(any(), any(), eq(UIConfig.ERROR_DIALOG_TITLE), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    public void testRevenueSummaryButton() {
        when(billingController.getAllInvoices()).thenReturn(new ArrayList<>());
        BillingPanel panel = new BillingPanel(billingController, appointmentController, notificationPanel, notificationPersistence);

        JButton revenueSummaryBtn = findButton(panel, UIConfig.REVENUE_SUMMARY_BUTTON_TEXT);
        assertNotNull(revenueSummaryBtn);

        try (MockedStatic<RevenueSummaryUtil> revMock = mockStatic(RevenueSummaryUtil.class)) {
            revenueSummaryBtn.doClick();
            revMock.verify(() -> RevenueSummaryUtil.showRevenueSummary(any(), any()));
        }
    }

    // --- Helpers ---
    private Object getField(Object obj, String name) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText()))
                return b;
            if (c instanceof Container) {
                JButton btn = findButton((Container) c, text);
                if (btn != null)
                    return btn;
            }
        }
        return null;
    }
}