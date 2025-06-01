package physicianconnect.presentation;

import physicianconnect.objects.TimeSlot;
import physicianconnect.logic.AvailabilityService;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shows a weekly grid: a left‐hand time column (08:00–16:30)
 * plus seven day columns (Mon–Sun). Each day column has 16 slots.
 */
public class WeeklyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private LocalDate weekStart;
    private Map<LocalDate, java.util.List<TimeSlot>> weekData;

    // Constants
    private static final int DAYS_IN_WEEK = 7;
    private static final int SLOT_COUNT = 16;           // 08:00–16:30
    private static final int PIXEL_PER_SLOT = 30;       // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;     // width of left‐hand time column
    private static final int DAY_COLUMN_WIDTH = 100;    // width of each day‐of‐week column
    private static final int HEADER_HEIGHT = 30;        // height of the day‐header row

    public WeeklyAvailabilityPanel(int physicianId,
                                   AvailabilityService svc,
                                   LocalDate monday) {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.weekStart = monday;

        // Total width = time‐column + 7 day‐columns
        int totalW = TIME_LABEL_WIDTH + (DAYS_IN_WEEK * DAY_COLUMN_WIDTH);
        // Total height = header + 16 slots × 30px
        int totalH = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);
        setPreferredSize(new Dimension(totalW + 1, totalH + 1));

        loadWeek(monday);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // If click is inside the time‐label column or header row, ignore
                if (x < TIME_LABEL_WIDTH || y < HEADER_HEIGHT) {
                    return;
                }

                // Compute day index from x
                int dayIndex = (x - TIME_LABEL_WIDTH) / DAY_COLUMN_WIDTH;
                if (dayIndex < 0 || dayIndex >= DAYS_IN_WEEK) {
                    return;
                }
                // Compute slot index from y
                int slotIndex = (y - HEADER_HEIGHT) / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                LocalDate clickedDate = weekStart.plusDays(dayIndex);
                TimeSlot ts = weekData.get(clickedDate).get(slotIndex);
                if (ts.isBooked()) {
                    JOptionPane.showMessageDialog(WeeklyAvailabilityPanel.this,
                            clickedDate + " " + ts.getStart().toLocalTime()
                                    + "\nBooked by: " + ts.getPatientName());
                } else {
                    JOptionPane.showMessageDialog(WeeklyAvailabilityPanel.this,
                            clickedDate + " " + ts.getStart().toLocalTime()
                                    + "\nFREE slot.");
                }
            }
        });
    }

    /**
     * Loads a map of 7 days → 16 slots each. On SQLException, fallback to all free.
     */
    public void loadWeek(LocalDate monday) {
        this.weekStart = monday;
        try {
            this.weekData = availabilityService.getWeeklyAvailability(physicianId, monday);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Map<LocalDate, java.util.List<TimeSlot>> fallback = new LinkedHashMap<>();
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                LocalDate day = monday.plusDays(i);
                java.util.List<TimeSlot> slots = TimeSlot.generateDailySlots(day);
                fallback.put(day, slots);
            }
            this.weekData = fallback;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1) Draw the time‐label column
        g.setColor(new Color(230, 230, 230)); // light gray background
        g.fillRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT));
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT));

        // Draw header cell for the time‐column (optional label)
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT);
        // (Optionally, g.drawString("Time", 10, 20); )

        // Draw each of the 16 half‐hour time labels
        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // 2) Draw the 7 day columns (header + 16 slots) to the right
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            int x = TIME_LABEL_WIDTH + (day * DAY_COLUMN_WIDTH);
            LocalDate date = weekStart.plusDays(day);

            // Draw header cell
            g.setColor(new Color(245, 245, 245)); // off‐white background
            g.fillRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            String header = date.getDayOfWeek().toString().substring(0,3)
                    + " " + date.getMonthValue() + "/" + date.getDayOfMonth();
            g.drawString(header, x + 5, 20);

            // Draw each of the 16 slots below
            java.util.List<TimeSlot> slots = weekData.get(date);
            for (int i = 0; i < SLOT_COUNT; i++) {
                int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
                TimeSlot ts = slots.get(i);
                Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
                g.setColor(fill);
                g.fillRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);
            }
        }
    }
}