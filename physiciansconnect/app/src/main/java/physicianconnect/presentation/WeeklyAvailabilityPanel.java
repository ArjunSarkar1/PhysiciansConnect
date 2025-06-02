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
import java.util.Map;

/**
 * Shows a weekly grid: a left‐hand time column (08:00–16:30)
 * plus seven day columns (Mon–Sun), each with 16 slots.
 *
 * If a slot is booked, it draws the patient’s name inside that day‐cell.
 * Whenever an appointment is added/edited/deleted, it fires onSuccessCallback.
 */
public class WeeklyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentManager appointmentManager;
    private final Runnable onSuccessCallback;   // NEW: callback to reload dashboard

    private LocalDate weekStart;
    private Map<LocalDate, java.util.List<TimeSlot>> weekData;

    // Constants
    private static final int DAYS_IN_WEEK = 7;
    private static final int SLOT_COUNT = 16;           // 08:00–16:30
    private static final int PIXEL_PER_SLOT = 30;       // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;     // width of left‐hand time column
    private static final int DAY_COLUMN_WIDTH = 100;    // width of each day‐column
    private static final int HEADER_HEIGHT = 30;        // height of the day‐header row

    /**
     * @param physicianId        physician’s ID
     * @param svc                AvailabilityService (to fetch weekly slots)
     * @param apptMgr            AppointmentManager (to add/update/delete)
     * @param monday             the Monday that starts this week
     * @param onSuccessCallback  runnable to invoke after adding/updating/deleting
     */
    public WeeklyAvailabilityPanel(int physicianId,
                                   AvailabilityService svc,
                                   AppointmentManager apptMgr,
                                   LocalDate monday,
                                   Runnable onSuccessCallback)
    {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.appointmentManager = apptMgr;
        this.onSuccessCallback = onSuccessCallback;
        this.weekStart = monday;

        // Compute total preferred size:
        int totalW = TIME_LABEL_WIDTH + (DAYS_IN_WEEK * DAY_COLUMN_WIDTH);
        int totalH = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);
        setPreferredSize(new Dimension(totalW + 1, totalH + 1));

        // Load initial week’s data
        loadWeek(monday);

        // Mouse listener for clicks on either a free or booked slot:
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If click is in the time‐label column (leftmost) or header row (top), ignore
                if (x < TIME_LABEL_WIDTH || y < HEADER_HEIGHT) {
                    return;
                }

                // (2) Compute dayIndex (0..6) and slotIndex (0..15)
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
                    // → FREE slot: ask user if they want to add an appointment
                    int choice = JOptionPane.showConfirmDialog(
                            WeeklyAvailabilityPanel.this,
                            "Slot on " + clickedDate + " at " + slotTime.toLocalTime()
                                    + " is free.\nAdd an appointment?",
                            "Add Appointment",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        // (3a) Open AddAppointmentDialog, passing a callback that:
                        //      • reloads *this* week’s slots
                        //      • then invokes the dashboard’s reloadEverything
                        AddAppointmentDialog addDlg = new AddAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(WeeklyAvailabilityPanel.this),
                                appointmentManager,
                                String.valueOf(physicianId),
                                () -> {
                                    // First, reload this week’s data:
                                    loadWeek(weekStart);
                                    // Then inform the dashboard to reload EVERYTHING:
                                    if (onSuccessCallback != null) {
                                        onSuccessCallback.run();
                                    }
                                }
                        );
                        // Pre‐fill spinners to the clicked slot time:
                        java.util.Date prefill = java.util.Date.from(
                                slotTime.atZone(ZoneId.systemDefault()).toInstant()
                        );
                        addDlg.dateSpinner.setValue(prefill);
                        addDlg.timeSpinner.setValue(prefill);

                        addDlg.setVisible(true);
                    }
                }
                else {
                    // → BOOKED slot: find the existing Appointment by matching date/time
                    Appointment existingAppt = null;
                    for (Appointment a : appointmentManager.getAppointmentsForPhysician(String.valueOf(physicianId))) {
                        if (a.getDateTime().equals(slotTime)) {
                            existingAppt = a;
                            break;
                        }
                    }

                    if (existingAppt != null) {
                        // (3b) Open ViewAppointmentDialog. Pass a callback that:
                        //      • reloads *this* week’s data
                        //      • then invokes the dashboard’s reloadEverything
                        ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(WeeklyAvailabilityPanel.this),
                                appointmentManager,
                                existingAppt,
                                () -> {
                                    // First, reload this week’s data:
                                    loadWeek(weekStart);
                                    // Then inform the dashboard to reload EVERYTHING:
                                    if (onSuccessCallback != null) {
                                        onSuccessCallback.run();
                                    }
                                }
                        );
                        viewDlg.setVisible(true);
                    }
                    else {
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
     * Loads one week’s worth of data, catching SQLException if anything goes wrong.
     * If the DAO fails, it falls back to “all free” for each day.
     */
    public void loadWeek(LocalDate monday) {
        this.weekStart = monday;
        try {
            this.weekData = availabilityService.getWeeklyAvailability(physicianId, monday);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            // Fallback: build a map of all‐free slots each day
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

        // ─── (1) Draw left‐hand time column ───
        g.setColor(new Color(230, 230, 230)); // light gray background
        g.fillRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT));
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT));

        // Header cell (label “Time” could go here if you like)
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT);

        // Draw each of the 16 time labels (08:00, 08:30, …, 16:30)
        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // ─── (2) Draw the 7 day columns (header + 16 slots), showing patient names if booked ───
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            int x = TIME_LABEL_WIDTH + (day * DAY_COLUMN_WIDTH);
            LocalDate date = weekStart.plusDays(day);

            // 2a) Draw header cell for each day
            g.setColor(new Color(245, 245, 245)); // off‐white background
            g.fillRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);

            String header = date.getDayOfWeek().toString().substring(0,3)
                    + " " + date.getMonthValue() + "/" + date.getDayOfMonth();
            g.drawString(header, x + 5, 20);

            // 2b) Draw each of the 16 half‐hour slots
            java.util.List<TimeSlot> slots = weekData.get(date);
            for (int i = 0; i < SLOT_COUNT; i++) {
                int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
                TimeSlot ts = slots.get(i);

                // Fill background: gray if booked, white otherwise
                Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
                g.setColor(fill);
                g.fillRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                // Draw border
                g.setColor(Color.BLACK);
                g.drawRect(x, y, DAY_COLUMN_WIDTH, PIXEL_PER_SLOT);

                // If booked, draw the patient’s name in that cell
                if (ts.isBooked()) {
                    g.setColor(Color.BLACK);
                    String patient = ts.getPatientName();
                    // Truncate if too long
                    String display = (patient.length() > 12)
                            ? patient.substring(0, 9) + "…"
                            : patient;
                    g.drawString(display, x + 5, y + 18);
                }
            }
        }
    }
}