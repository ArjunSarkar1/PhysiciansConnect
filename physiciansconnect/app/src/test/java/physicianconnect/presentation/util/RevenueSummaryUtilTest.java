package physicianconnect.presentation.util;


import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import physicianconnect.objects.Invoice;
import java.awt.image.BufferedImage;
import physicianconnect.presentation.config.UIConfig;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RevenueSummaryUtilTest {

    @Test
    void testChartPanelPaintComponentIsCovered() {
        Invoice inv1 = mock(Invoice.class);
        Invoice inv2 = mock(Invoice.class);
        when(inv1.getTotalAmount()).thenReturn(100.0);
        when(inv1.getBalance()).thenReturn(40.0);
        when(inv2.getTotalAmount()).thenReturn(200.0);
        when(inv2.getBalance()).thenReturn(0.0);

        List<Invoice> invoices = List.of(inv1, inv2);

        // Intercept the panel and extract chartPanel
        try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
            RevenueSummaryUtil.showRevenueSummary(null, invoices);

            paneMock.verify(() -> JOptionPane.showMessageDialog(
                isNull(),
                argThat(panel -> {
                    if (!(panel instanceof JPanel mainPanel)) return false;
                    Component[] comps = mainPanel.getComponents();
                    JPanel chartPanel = null;
                    for (Component c : comps) {
                        if (c instanceof JPanel p && p.getPreferredSize() != null && p.getPreferredSize().width == 400) {
                            chartPanel = p;
                        }
                    }
                    // Actually call paintComponent to cover the code
                    if (chartPanel != null) {
                        chartPanel.setSize(420, 200); // ensure getWidth/getHeight > 0
                        Graphics2D g = (Graphics2D) new BufferedImage(420, 200, BufferedImage.TYPE_INT_RGB).getGraphics();
                        chartPanel.paint(g); // This will call paintComponent
                        return true;
                    }
                    return false;
                }),
                eq(UIConfig.REVENUE_SUMMARY_DIALOG_TITLE),
                eq(JOptionPane.PLAIN_MESSAGE)
            ));
        }
    }
}