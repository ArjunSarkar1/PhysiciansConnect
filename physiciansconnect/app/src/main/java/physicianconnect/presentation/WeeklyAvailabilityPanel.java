package physicianconnect.presentation;

import physicianconnect.logic.AvailabilityService;
import physicianconnect.logic.AppointmentManager;
import physicianconnect.objects.TimeSlot;
import physicianconnect.objects.Appointment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows a weekly grid: a left‐hand time column (08:00–16:30)
 * plus seven day columns (Mon–Sun), each with 16 slots.
 *
 * Now includes a Runnable onWeekChanged callback so that any
 * add/update/delete inside the weekly view can notify another
 * component (e.g., the daily view) to reload itself.
 */
public class WeeklyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentManager appointmentManager;
    private final Runnable onWeekChanged;       // new callback
    private LocalDate weekStart;
    private Map<LocalDate, List<TimeSlot>> weekData;

    // Constants
    private static final int DAYS_IN_WEEK     = 7;
    private static final int SLOT_COUNT       = 16;   // 08:00–16:30
    private static final int PIXEL_PER_SLOT   = 30;   // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;   // width of left‐hand time column
    private static final int DAY_COLUMN_WIDTH = 100;  // width of each day‐of‐week column
    private static final int HEADER_HEIGHT    = 30;   // height of the day‐header row

    /**
     * @param physicianId      the physician’s integer ID
     * @param svc              the AvailabilityService
     * @param apptMgr          the AppointmentManager
     * @param monday           the LocalDate representing the Monday of the week to display
     * @param onWeekChanged    callback to run after any appointment add/update/delete inside weekly view
     */
    public WeeklyAvailabilityPanel(int physicianId,
                                   AvailabilityService svc,
                                   AppointmentManager apptMgr,
                                   LocalDate monday,
                                   Runnable onWeekChanged) {
        this.physicianId         = physicianId;
        this.availabilityService = svc;
        this.appointmentManager  = apptMgr;
        this.onWeekChanged       = onWeekChanged;
        this.weekStart           = monday;

        // Calculate preferred size
        int totalW = TIME_LABEL_WIDTH + (DAYS_IN_WEEK * DAY_COLUMN_WIDTH);
        int totalH = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);
        setPreferredSize(new Dimension(totalW + 1, totalH + 1));

        // Load initial week data
        loadWeek(monday);

        // Mouse listener for clicks (free vs. booked slots)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If click is in the time‐label column or header row, ignore
                if (x < TIME_LABEL_WIDTH || y < HEADER_HEIGHT) {
                    return;
                }

                // (2) Determine which day column (0..6) and which slot row (0..15)
                int dayIndex = (x - TIME_LABEL_WIDTH) / DAY_COLUMN_WIDTH;
                if (dayIndex < 0 || dayIndex >= DAYS_IN_WEEK) {
                    return;
                }
                int slotIndex = (y - HEADER_HEIGHT) / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                LocalDate clickedDate = weekStart.plusDays(dayIndex);
                TimeSlot ts = weekData.get(clickedDate).get(slotIndex);
                LocalDateTime slotTime = ts.getStart();

                if (!ts.isBooked()) {
                    // → FREE slot: confirm & open AddAppointmentDialog
                    int choice = JOptionPane.showConfirmDialog(
                            WeeklyAvailabilityPanel.this,
                            "Slot on " + clickedDate + " at " + slotTime.toLocalTime()
                                    + " is free.\nAdd an appointment?",
                            "Add Appointment",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        AddAppointmentDialog addDlg = new AddAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(WeeklyAvailabilityPanel.this),
                                appointmentManager,
                                String.valueOf(physicianId),
                                () -> {
                                    // 1) reload this weekly grid
                                    loadWeek(weekStart);
                                    // 2) notify the onWeekChanged listener
                                    onWeekChanged.run();
                                }
                        );
                        java.util.Date prefill = java.util.Date.from(
                                slotTime.atZone(ZoneId.systemDefault()).toInstant()
                        );
                        addDlg.dateSpinner.setValue(prefill);
                        addDlg.timeSpinner.setValue(prefill);
                        addDlg.setVisible(true);
                    }
                } else {
                    // → BOOKED slot: find the matching Appointment by date/time
                    Appointment existingAppt = null;
                    for (Appointment a : appointmentManager.getAppointmentsForPhysician(String.valueOf(physicianId))) {
                        if (a.getDateTime().equals(slotTime)) {
                            existingAppt = a;
                            break;
                        }
                    }
                    if (existingAppt != null) {
                        ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(WeeklyAvailabilityPanel.this),
                                appointmentManager,
                                existingAppt,
                                () -> {
                                    // 1) reload this weekly grid
                                    loadWeek(weekStart);
                                    // 2) notify the onWeekChanged listener
                                    onWeekChanged.run();
                                }
                        );
                        viewDlg.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(
                                WeeklyAvailabilityPanel.this,
                                "Error: could not find appointment to edit.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
    }

    /**
     * Loads a map of seven days → sixteen slots each. On SQLException, falls back to “all free.”
     */
    public void loadWeek(LocalDate monday) {
        this.weekStart = monday;
        try {
            this.weekData = availabilityService.getWeeklyAvailability(
                    String.valueOf(physicianId),
                    monday
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            Map<LocalDate, List<TimeSlot>> fallback = new LinkedHashMap<>();
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                LocalDate day = monday.plusDays(i);
                List<TimeSlot> slots = TimeSlot.generateDailySlots(day);
                fallback.put(day, slots);
            }
            this.weekData = fallback;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ─── (1) Draw left‐hand time column ───
        g.setColor(new Color(230, 230, 230));
        int totalHeight = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);
        g.fillRect(0, 0, TIME_LABEL_WIDTH, totalHeight);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, totalHeight);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT);

        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // ─── (2) Draw each day‐column header & slots ───
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            int x = TIME_LABEL_WIDTH + (day * DAY_COLUMN_WIDTH);
            LocalDate date = weekStart.plusDays(day);

            // 2a) header background + border + weekday label
            g.setColor(new Color(245, 245, 245));
            g.fillRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            String header = date.getDayOfWeek().toString().substring(0, 3)
                    + " " + date.getMonthValue() + "/" + date.getDayOfMonth();
            g.drawString(header, x + 5, 20);

            // 2b) sixteen half‐hour slots
            List<TimeSlot> slots = weekData.get(date);
            for (int i = 0; i < SLOT_COUNT; i++) {
                int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
                TimeSlot ts = slots.get(i);

                // Fill background (gray if booked, else white)
                Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
                g.setColor(fill);
                g.fillRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                // Draw border
                g.setColor(Color.BLACK);
                g.drawRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                // If booked, draw patient name
                if (ts.isBooked()) {
                    g.setColor(Color.BLACK);
                    String patient = ts.getPatientName();
                    String display = patient.length() > 12
                            ? patient.substring(0, 9) + "…"
                            : patient;
                    g.drawString(display, x + 5, y + 18);
                }
            }
        }
    }
}