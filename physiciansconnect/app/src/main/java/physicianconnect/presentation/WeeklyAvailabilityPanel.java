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
 */
public class WeeklyAvailabilityPanel extends JPanel {
    private final String physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentManager appointmentManager;
    private LocalDate weekStart;
    private Map<LocalDate, java.util.List<TimeSlot>> weekData;

    // Constants
    private static final int DAYS_IN_WEEK = 7;
    private static final int SLOT_COUNT = 16;           // 08:00–16:30
    private static final int PIXEL_PER_SLOT = 30;       // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;     // width of left‐hand time column
    private static final int DAY_COLUMN_WIDTH = 100;    // width of each day‐of‐week column
    private static final int HEADER_HEIGHT = 30;        // height of the day‐header row

    public WeeklyAvailabilityPanel(String physicianId,
                                   AvailabilityService svc,
                                   AppointmentManager apptMgr,
                                   LocalDate monday) {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.appointmentManager = apptMgr;
        this.weekStart = monday;

        // Total width = time‐column + 7 day‐columns
        int totalW = TIME_LABEL_WIDTH + (DAYS_IN_WEEK * DAY_COLUMN_WIDTH);
        // Total height = header + 16 slots × 30px
        int totalH = HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT);
        setPreferredSize(new Dimension(totalW + 1, totalH + 1));

        loadWeek(monday);

        // Mouse listener for add/edit
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If click is in the time‐label column or header row, ignore
                if (x < TIME_LABEL_WIDTH || y < HEADER_HEIGHT) {
                    return;
                }

                // (2) Compute which day‐column (0..6) and which slot‐row (0..15)
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
                                () -> loadWeek(weekStart)   // callback reloads weekly grid
                        );
                        java.util.Date prefill = java.util.Date.from(
                                slotTime.atZone(ZoneId.systemDefault()).toInstant()
                        );
                        addDlg.dateSpinner.setValue(prefill);
                        addDlg.timeSpinner.setValue(prefill);

                        addDlg.setVisible(true);
                    }
                }
                else {
                    // → BOOKED slot: find existing Appointment by matching date/time
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
                                () -> loadWeek(weekStart)   // callback reloads weekly grid
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

        // ─── (1) Draw left‐hand time column ───
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT));
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT + (SLOT_COUNT * PIXEL_PER_SLOT));
        g.drawRect(0, 0, TIME_LABEL_WIDTH, HEADER_HEIGHT);

        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // ─── (2) Draw each day‐column header & slots, showing patient name if booked ───
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            int x = TIME_LABEL_WIDTH + (day * DAY_COLUMN_WIDTH);
            LocalDate date = weekStart.plusDays(day);

            // 2a) header background + border + weekday label
            g.setColor(new Color(245, 245, 245));
            g.fillRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, DAY_COLUMN_WIDTH, HEADER_HEIGHT);
            String header = date.getDayOfWeek().toString().substring(0,3)
                    + " " + date.getMonthValue() + "/" + date.getDayOfMonth();
            g.drawString(header, x + 5, 20);

            // 2b) each of the 16 half‐hour slots
            java.util.List<TimeSlot> slots = weekData.get(date);
            for (int i = 0; i < SLOT_COUNT; i++) {
                int y = HEADER_HEIGHT + (i * PIXEL_PER_SLOT);
                TimeSlot ts = slots.get(i);

                // Draw background (gray if booked, else white)
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
                    String display = patient.length() > 12 ? patient.substring(0, 9) + "…" : patient;
                    g.drawString(display, x + 5, y + 18);
                }
            }
        }
    }
}