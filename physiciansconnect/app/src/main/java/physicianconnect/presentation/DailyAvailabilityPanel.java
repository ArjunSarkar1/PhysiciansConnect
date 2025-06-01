package physicianconnect.presentation;

import physicianconnect.objects.TimeSlot;
import physicianconnect.logic.AvailabilityService; // note: logic package
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class DailyAvailabilityPanel extends JPanel {
    private final int physicianId;                 // still an int
    private final AvailabilityService availabilityService;
    private LocalDate currentDate;
    private List<TimeSlot> currentSlots;

    private static final int SLOT_COUNT = 17;       // 9am–5pm in 30-min increments
    private static final int PIXEL_PER_SLOT = 30;   // each slot is 30px tall
    private static final int SLOT_WIDTH = 200;      // width in px

    public DailyAvailabilityPanel(int physicianId,
                                  AvailabilityService svc,
                                  LocalDate date) {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.currentDate = date;
        setPreferredSize(new Dimension(SLOT_WIDTH + 1, SLOT_COUNT * PIXEL_PER_SLOT + 1));

        // Load the slots now (catching SQL exceptions)
        loadSlotsForDate(date);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int y = e.getY();
                int slotIndex = y / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) return;

                TimeSlot ts = currentSlots.get(slotIndex);
                if (!ts.isBooked()) {
                    JOptionPane.showMessageDialog(DailyAvailabilityPanel.this,
                            "Free slot: " + ts.getStart().toLocalTime());
                } else {
                    JOptionPane.showMessageDialog(DailyAvailabilityPanel.this,
                            "Booked by: " + ts.getPatientName()
                                    + "\nTime: " + ts.getStart().toLocalTime());
                }
            }
        });
    }

    /**
     * Call this whenever you want to load a new date’s availability.
     * Internally, wrap the SQLException in a try/catch and default to “all free” on error.
     */
    public void loadSlotsForDate(LocalDate date) {
        this.currentDate = date;

        try {
            this.currentSlots = availabilityService.getDailyAvailability(physicianId, date);
        } catch (SQLException ex) {
            // On any SQL error, log it and show all slots as free (or show an error message).
            ex.printStackTrace();
            // Create an “all free” list rather than crashing:
            List<TimeSlot> fallback = new ArrayList<>();
            for (TimeSlot ts : TimeSlot.generateDailySlots(date)) {
                ts.setBooked(false);
                fallback.add(ts);
            }
            this.currentSlots = fallback;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < SLOT_COUNT; i++) {
            TimeSlot ts = currentSlots.get(i);
            int y = i * PIXEL_PER_SLOT;
            Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
            g.setColor(fill);
            g.fillRect(0, y, SLOT_WIDTH, PIXEL_PER_SLOT);
            g.setColor(Color.BLACK);
            g.drawRect(0, y, SLOT_WIDTH, PIXEL_PER_SLOT);

            // Draw the “HH:MM” label inside the rectangle
            g.setColor(Color.BLACK);
            String timeLabel = ts.getStart().toLocalTime().toString();
            g.drawString(timeLabel, 5, y + 15);
        }
    }
}
