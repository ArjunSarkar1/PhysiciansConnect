package physicianconnect.presentation;

import physicianconnect.logic.controller.BillingController;
import physicianconnect.objects.Invoice;
import physicianconnect.objects.Payment;
import physicianconnect.objects.ServiceItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillingPanel extends JPanel {
    private final BillingController billingController;
    private final DefaultTableModel model;
    private final JTable invoiceTable;

    public BillingPanel(BillingController billingController) {
        this.billingController = billingController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton newInvoiceBtn = new JButton("New Invoice");
        JButton revenueSummaryBtn = new JButton("Revenue Summary");

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(newInvoiceBtn);
        topBar.add(revenueSummaryBtn);

        add(topBar, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Patient", "Total", "Balance", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        invoiceTable = new JTable(model);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshInvoices();

        add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        newInvoiceBtn.addActionListener(e -> showNewInvoiceDialog());
        revenueSummaryBtn.addActionListener(e -> showRevenueSummary());

        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                String invoiceId = (String) model.getValueAt(invoiceTable.getSelectedRow(), 0);
                Invoice invoice = billingController.getAllInvoices().stream()
                        .filter(inv -> inv.getId().equals(invoiceId)).findFirst().orElse(null);
                if (invoice != null) {
                    showInvoiceDetail(invoice);
                }
            }
        });
    }

    private void refreshInvoices() {
        model.setRowCount(0);
        List<Invoice> invoices = billingController.getAllInvoices();
        for (Invoice inv : invoices) {
            model.addRow(new Object[]{
                    inv.getId(),
                    inv.getPatientName(),
                    inv.getTotalAmount(),
                    inv.getBalance(),
                    inv.getStatus()
            });
        }
    }

    private void showNewInvoiceDialog() {
        JTextField appointmentIdField = new JTextField();
        JTextField patientNameField = new JTextField();
        JTextField servicesField = new JTextField();
        JTextField insuranceAdjField = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Appointment ID:"));
        panel.add(appointmentIdField);
        panel.add(new JLabel("Patient Name:"));
        panel.add(patientNameField);
        panel.add(new JLabel("Services (e.g. Consult:100,Lab:50):"));
        panel.add(servicesField);
        panel.add(new JLabel("Insurance Adjustment:"));
        panel.add(insuranceAdjField);

        int result = JOptionPane.showConfirmDialog(this, panel, "New Invoice", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String appointmentId = appointmentIdField.getText().trim();
                String patientName = patientNameField.getText().trim();
                String servicesStr = servicesField.getText().trim();
                double insuranceAdj = Double.parseDouble(insuranceAdjField.getText().trim());

                List<ServiceItem> services = ServiceItem.parseList(servicesStr); // You may need to implement this utility

                billingController.createInvoice(appointmentId, patientName, services, insuranceAdj);
                refreshInvoices();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating invoice: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showInvoiceDetail(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice ID: ").append(invoice.getId()).append("\n");
        sb.append("Patient: ").append(invoice.getPatientName()).append("\n");
        sb.append("Appointment ID: ").append(invoice.getAppointmentId()).append("\n");
        sb.append("Services:\n");
        for (ServiceItem s : invoice.getServices()) {
            sb.append("  - ").append(s.getName()).append(": $").append(s.getCost()).append("\n");
        }
        sb.append("Insurance Adjustment: $").append(invoice.getInsuranceAdjustment()).append("\n");
        sb.append("Total: $").append(invoice.getTotalAmount()).append("\n");
        sb.append("Balance: $").append(invoice.getBalance()).append("\n");
        sb.append("Status: ").append(invoice.getStatus()).append("\n");
        sb.append("Created: ").append(invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);

        if (!"Paid".equals(invoice.getStatus())) {
            JButton payBtn = new JButton("Record Payment");
            payBtn.addActionListener(e -> {
                showPaymentDialog(invoice);
            });
            panel.add(payBtn, BorderLayout.SOUTH);
        }

        JOptionPane.showMessageDialog(this, panel, "Invoice Details", JOptionPane.INFORMATION_MESSAGE);
        refreshInvoices();
    }

    private void showPaymentDialog(Invoice invoice) {
        JTextField amountField = new JTextField();
        JTextField methodField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Method (e.g. Cash, Card):"));
        panel.add(methodField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Record Payment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String method = methodField.getText().trim();
                billingController.recordPayment(invoice.getId(), amount, method);
                refreshInvoices();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error recording payment: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRevenueSummary() {
        List<Invoice> invoices = billingController.getAllInvoices();
        double totalBilled = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalPaid = invoices.stream().mapToDouble(inv -> inv.getTotalAmount() - inv.getBalance()).sum();
        double outstanding = invoices.stream().mapToDouble(Invoice::getBalance).sum();

        String summary = String.format(
                "Total Billed: $%.2f\nTotal Paid: $%.2f\nOutstanding: $%.2f",
                totalBilled, totalPaid, outstanding
        );
        JOptionPane.showMessageDialog(this, summary, "Revenue Summary", JOptionPane.INFORMATION_MESSAGE);
    }
}