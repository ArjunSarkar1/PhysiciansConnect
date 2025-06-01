package physicianconnect.presentation;

import physicianconnect.objects.TimeSlot;
import physicianconnect.logic.AvailabilityService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.SQLException;
import java.util.List;

/**
 * Shows a single day’s 16 half‐hour slots (08:00–16:30) in one column,
 * with a dedicated left‐hand column for the time labels.
 */
public class DailyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private LocalDate currentDate;
    private List<TimeSlot> currentSlots;

    //Constants
    // 16 slots (08:00, 08:30, …, 16:30)
    private static final int SLOT_COUNT = 16;
    private static final int PIXEL_PER_SLOT = 30;      // each slot row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;    // width of the left‐hand time column
    private static final int SLOT_COLUMN_WIDTH = 200;  // width of the one‐day slot column

    public DailyAvailabilityPanel(int physicianId,
                                  AvailabilityService svc,
                                  LocalDate date) {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.currentDate = date;

        // Total width = time‐column + one slot column
        int totalWidth = TIME_LABEL_WIDTH + SLOT_COLUMN_WIDTH;
        // Total height = 16 slots × 30px each
        int totalHeight = SLOT_COUNT * PIXEL_PER_SLOT;
        setPreferredSize(new Dimension(totalWidth + 1, totalHeight + 1));

        loadSlotsForDate(date);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // If click is inside the time‐label column, do nothing
                if (x < TIME_LABEL_WIDTH) {
                    return;
                }
                // Compute which slot‐row was clicked
                int slotIndex = y / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                TimeSlot ts = currentSlots.get(slotIndex);
                if (ts.isBooked()) {
                    JOptionPane.showMessageDialog(DailyAvailabilityPanel.this,
                            ts.getStart().toLocalDate()
                                    + " " + ts.getStart().toLocalTime()
                                    + "\nBooked by: " + ts.getPatientName());
                } else {
                    JOptionPane.showMessageDialog(DailyAvailabilityPanel.this,
                            ts.getStart().toLocalDate()
                                    + " " + ts.getStart().toLocalTime()
                                    + "\nFREE slot.");
                }
            }
        });
    }

    /**
     * Loads 16 half‐hour slots for `date`. If the DAO throws SQLException,
     * fallback to a simple “all‐free” list via generateDailySlots().
     */
    public void loadSlotsForDate(LocalDate date) {
        this.currentDate = date;
        try {
            this.currentSlots = availabilityService.getDailyAvailability(physicianId, date);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Fallback: generate a list of free slots (08:00–16:30)
            this.currentSlots = TimeSlot.generateDailySlots(date);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the time‐label column
        g.setColor(new Color(230, 230, 230)); // light gray background
        g.fillRect(0, 0, TIME_LABEL_WIDTH, SLOT_COUNT * PIXEL_PER_SLOT);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, SLOT_COUNT * PIXEL_PER_SLOT);

        LocalTime timeCursor = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            String label = timeCursor.toString(); // “08:00”, “08:30”, …
            g.drawString(label, 10, y + 20);
            timeCursor = timeCursor.plusMinutes(30);
        }

        // Draw the slot column to the right of the time labels
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            TimeSlot ts = currentSlots.get(i);
            Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
            g.setColor(fill);
            g.fillRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            g.setColor(Color.BLACK);
            g.drawRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);
        }
    }
}
