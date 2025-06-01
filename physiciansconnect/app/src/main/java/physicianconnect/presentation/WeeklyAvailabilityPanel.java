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
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.SQLException;

/**
 * A Swing panel that shows 7 columns (Mon–Sun), each with 16 half-hour slots (9am–5pm).
 */
public class WeeklyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private LocalDate weekStart;
    private Map<LocalDate, java.util.List<TimeSlot>> weekData;

    private static final int DAYS_IN_WEEK = 7;
    private static final int SLOT_COUNT = 17;           // 16 half-hour slots 9–17
    private static final int PIXEL_PER_SLOT = 25;       // each row is 25px tall
    private static final int DAY_COLUMN_WIDTH = 100;    // each column is 100px wide

    public WeeklyAvailabilityPanel(int physicianId,
                                   AvailabilityService svc,
                                   LocalDate monday) {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.weekStart = monday;
        setPreferredSize(new Dimension(
                DAYS_IN_WEEK * DAY_COLUMN_WIDTH + 1,
                SLOT_COUNT * PIXEL_PER_SLOT + 30 // +30 for header row
        ));
        loadWeek(monday);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY() - 30;  // subtract header height
                if (y < 0) return;      // clicked on header

                int dayIndex  = x / DAY_COLUMN_WIDTH;
                int slotIndex = y / PIXEL_PER_SLOT;
                if (dayIndex < 0 || dayIndex >= DAYS_IN_WEEK ||
                        slotIndex < 0 || slotIndex >= SLOT_COUNT) return;

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
     * Loads one week’s worth of slots, catching SQLException if anything goes wrong.
     */
    public void loadWeek(LocalDate monday) {
        this.weekStart = monday;
        try {
            this.weekData = availabilityService.getWeeklyAvailability(physicianId, monday);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Fallback: build a map of all‐free slots for each day
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
        // Draw header row (day names)
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            int x = day * DAY_COLUMN_WIDTH;
            LocalDate date = weekStart.plusDays(day);
            String header = date.getDayOfWeek().toString().substring(0,3)
                    + " " + date.getMonthValue() + "/" + date.getDayOfMonth();
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, DAY_COLUMN_WIDTH, 30);
            g.drawString(header, x + 5, 20);
        }

        // Draw each 7×16 grid of slots
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            LocalDate date = weekStart.plusDays(day);
            java.util.List<TimeSlot> slots = weekData.get(date);

            for (int i = 0; i < SLOT_COUNT; i++) {
                int x = day * DAY_COLUMN_WIDTH;
                int y = 30 + (i * PIXEL_PER_SLOT);
                TimeSlot ts = slots.get(i);

                Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
                g.setColor(fill);
                g.fillRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                if (day == 0) {
                    String timeLabel = ts.getStart().toLocalTime().toString();
                    g.drawString(timeLabel, x + 2, y + 12);
                }
            }
        }
    }
}
