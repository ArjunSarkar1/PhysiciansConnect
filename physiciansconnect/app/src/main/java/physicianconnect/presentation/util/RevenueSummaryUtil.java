package physicianconnect.presentation.util;

import physicianconnect.objects.Invoice;
import physicianconnect.presentation.config.UIConfig;
import physicianconnect.presentation.config.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RevenueSummaryUtil {
        public interface RevenueSummaryListener {
        void onRevenueSummaryChanged();
    }

    private static final List<RevenueSummaryListener> listeners = new CopyOnWriteArrayList<>();

    public static void addListener(RevenueSummaryListener l) {
        listeners.add(l);
    }

    public static void removeListener(RevenueSummaryListener l) {
        listeners.remove(l);
    }

    public static void fireRevenueSummaryChanged() {
        for (RevenueSummaryListener l : listeners) {
            l.onRevenueSummaryChanged();
        }
    }

    public static void showRevenueSummary(Component parent, List<Invoice> invoices) {
        double totalBilled = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalPaid = invoices.stream().mapToDouble(inv -> inv.getTotalAmount() - inv.getBalance()).sum();
        double outstanding = invoices.stream().mapToDouble(Invoice::getBalance).sum();

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(3, 2, 12, 12));
        summaryPanel.setBackground(UITheme.BACKGROUND_COLOR);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.ACCENT_LIGHT_COLOR, 2, true),
                UIConfig.REVENUE_SUMMARY_TITLE,
                0, 0, UITheme.HEADER_FONT, UITheme.ACCENT_LIGHT_COLOR));

        JLabel billedLabel = new JLabel(UIConfig.TOTAL_BILLED_LABEL + ":");
        billedLabel.setFont(UITheme.LABEL_FONT);
        JLabel billedValue = makeSummaryValue(totalBilled, UITheme.PRIMARY_COLOR);

        JLabel paidLabel = new JLabel(UIConfig.TOTAL_PAID_LABEL + ":");
        paidLabel.setFont(UITheme.LABEL_FONT);
        JLabel paidValue = makeSummaryValue(totalPaid, totalPaid < 0 ? Color.RED : new Color(0, 128, 0));

        JLabel outstandingLabel = new JLabel(UIConfig.OUTSTANDING_LABEL + ":");
        outstandingLabel.setFont(UITheme.LABEL_FONT);
        JLabel outstandingValue = makeSummaryValue(outstanding,
                outstanding > 0 ? Color.RED : UITheme.ACCENT_LIGHT_COLOR);

        summaryPanel.add(billedLabel);
        summaryPanel.add(billedValue);
        summaryPanel.add(paidLabel);
        summaryPanel.add(paidValue);
        summaryPanel.add(outstandingLabel);
        summaryPanel.add(outstandingValue);

        // Bar chart (with negative support)
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = getWidth() - 40;
                int height = getHeight() - 60;
                int x = 20, y = 20;
                int barWidth = width / 3 - 20;
                double max = Math.max(Math.abs(totalBilled), Math.max(Math.abs(totalPaid), Math.abs(outstanding)));
                int zeroY = y + height / 2; // baseline for zero

                // Draw baseline
                g.setColor(Color.GRAY);
                g.drawLine(x - 10, zeroY, x + width, zeroY);

                double[] values = { totalBilled, totalPaid, outstanding };
                Color[] colors = { UITheme.PRIMARY_COLOR, new Color(0, 128, 0), Color.RED };
                String[] labels = { UIConfig.TOTAL_BILLED_LABEL, UIConfig.TOTAL_PAID_LABEL,
                        UIConfig.OUTSTANDING_LABEL };

                for (int i = 0; i < 3; i++) {
                    int barHeight = (int) ((height / 2) * (Math.abs(values[i]) / (max == 0 ? 1 : max)));
                    int barX = x + i * (barWidth + 20);
                    int barY = values[i] >= 0 ? zeroY - barHeight : zeroY;
                    g.setColor(colors[i]);
                    g.fillRoundRect(barX, barY, barWidth, barHeight, 12, 12);
                    g.setColor(Color.BLACK);
                    g.drawString(labels[i], barX, zeroY + barHeight + 20);
                }
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 160));
        chartPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(parent, panel, UIConfig.REVENUE_SUMMARY_DIALOG_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    private static JLabel makeSummaryValue(double value, Color color) {
        JLabel label = new JLabel((value < 0 ? "-$" : "$") + String.format("%.2f", Math.abs(value)));
        label.setFont(UITheme.HEADER_FONT);
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    // Add this method to RevenueSummaryUtil
    public static JPanel createSummaryPanel(List<Invoice> invoices) {
        double totalBilled = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double totalPaid = invoices.stream().mapToDouble(inv -> inv.getTotalAmount() - inv.getBalance()).sum();
        double outstanding = invoices.stream().mapToDouble(Invoice::getBalance).sum();

        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 8, 2));
        summaryPanel.setBackground(UITheme.BACKGROUND_COLOR);

        summaryPanel.add(new JLabel(UIConfig.TOTAL_BILLED_LABEL + ":"));
        summaryPanel.add(makeSummaryValue(totalBilled, UITheme.PRIMARY_COLOR));
        summaryPanel.add(new JLabel(UIConfig.TOTAL_PAID_LABEL + ":"));
        summaryPanel.add(makeSummaryValue(totalPaid, totalPaid < 0 ? Color.RED : new Color(0, 128, 0)));
        summaryPanel.add(new JLabel(UIConfig.OUTSTANDING_LABEL + ":"));
        summaryPanel.add(makeSummaryValue(outstanding, outstanding > 0 ? Color.RED : UITheme.ACCENT_LIGHT_COLOR));

        return summaryPanel;
    }
}