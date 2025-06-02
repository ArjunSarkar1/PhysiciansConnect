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
import java.util.List;

/**
 * Shows a single day’s 16 half‐hour slots (08:00–16:30) in one column,
 * with a dedicated left‐hand column for the time labels.
 *
 * If a slot is booked, it draws the patient’s name inside that slot.
 * Whenever an appointment is added/edited/deleted, it fires onSuccessCallback.
 */
public class DailyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentManager appointmentManager;
    private final Runnable onSuccessCallback;   // NEW: callback to reload dashboard

    private LocalDate currentDate;
    private List<TimeSlot> currentSlots;

    // Constants
    private static final int SLOT_COUNT = 16;          // 16 half‐hour slots (08:00–16:30)
    private static final int PIXEL_PER_SLOT = 30;      // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;    // width of left‐hand time column
    private static final int SLOT_COLUMN_WIDTH = 200;  // width of the slot column

    /**
     * @param physicianId        numeric ID of this physician
     * @param svc                AvailabilityService (to fetch daily slots)
     * @param apptMgr            AppointmentManager (to add/update/delete)
     * @param date               the date to show initially
     * @param onSuccessCallback  runnable to invoke after adding/updating/deleting
     */
    public DailyAvailabilityPanel(int physicianId,
                                  AvailabilityService svc,
                                  AppointmentManager apptMgr,
                                  LocalDate date,
                                  Runnable onSuccessCallback)
    {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.appointmentManager = apptMgr;
        this.currentDate = date;
        this.onSuccessCallback = onSuccessCallback;

        // Total width = time‐label column + slot column
        int totalWidth = TIME_LABEL_WIDTH + SLOT_COLUMN_WIDTH;
        // Total height = 16 slots × 30px
        int totalHeight = SLOT_COUNT * PIXEL_PER_SLOT;
        setPreferredSize(new Dimension(totalWidth + 1, totalHeight + 1));

        // Load initial slots
        loadSlotsForDate(date);

        // Mouse listener to handle clicks on free vs. booked slots:
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If clicked in the time‐label column, ignore:
                if (x < TIME_LABEL_WIDTH) {
                    return;
                }

                // (2) Determine which slot‐row (0..15) was clicked:
                int slotIndex = y / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                TimeSlot ts = currentSlots.get(slotIndex);
                LocalDateTime slotTime = ts.getStart();

                if (!ts.isBooked()) {
                    // → FREE slot: ask user if they want to add an appointment
                    int choice = JOptionPane.showConfirmDialog(
                            DailyAvailabilityPanel.this,
                            "Slot at " + slotTime.toLocalDate()
                                    + " " + slotTime.toLocalTime()
                                    + " is free.\nAdd an appointment?",
                            "Add Appointment",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        // (3a) Open AddAppointmentDialog. Pass a callback that:
                        //      • reloads this day’s slots
                        //      • then invokes the dashboard’s 'reloadEverything'
                        AddAppointmentDialog addDlg = new AddAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(DailyAvailabilityPanel.this),
                                appointmentManager,
                                String.valueOf(physicianId),
                                () -> {
                                    // Refresh this daily panel’s data
                                    loadSlotsForDate(currentDate);
                                    // Then inform the dashboard to reload EVERYTHING:
                                    if (onSuccessCallback != null) {
                                        onSuccessCallback.run();
                                    }
                                }
                        );

                        // Pre‐fill dateSpinner/timeSpinner so it matches the clicked slot:
                        java.util.Date prefill = java.util.Date.from(
                                slotTime.atZone(ZoneId.systemDefault()).toInstant()
                        );
                        addDlg.dateSpinner.setValue(prefill);
                        addDlg.timeSpinner.setValue(prefill);

                        addDlg.setVisible(true);
                    }
                }
                else {
                    // → BOOKED slot: find the existing Appointment by comparing date/time
                    Appointment existingAppt = null;
                    for (Appointment a : appointmentManager.getAppointmentsForPhysician(String.valueOf(physicianId))) {
                        if (a.getDateTime().equals(slotTime)) {
                            existingAppt = a;
                            break;
                        }
                    }

                    if (existingAppt != null) {
                        // (3b) Open ViewAppointmentDialog in “edit/delete” mode. Pass a callback that:
                        //      • reloads this day’s slots
                        //      • then invokes the dashboard’s 'reloadEverything'
                        ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(DailyAvailabilityPanel.this),
                                appointmentManager,
                                existingAppt,
                                () -> {
                                    // Refresh this daily panel’s data
                                    loadSlotsForDate(currentDate);
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
                                DailyAvailabilityPanel.this,
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
     * Call this whenever you want to load a new date’s availability.
     * If the DAO throws SQLException, fallback to “all free” on error.
     */
    public void loadSlotsForDate(LocalDate date) {
        this.currentDate = date;
        try {
            this.currentSlots = availabilityService.getDailyAvailability(physicianId, date);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            // On any SQL error, show all slots as free:
            this.currentSlots = TimeSlot.generateDailySlots(date);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ─── 1) Draw the left‐hand time labels ───
        g.setColor(new Color(230, 230, 230)); // light gray background
        g.fillRect(0, 0, TIME_LABEL_WIDTH, SLOT_COUNT * PIXEL_PER_SLOT);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, TIME_LABEL_WIDTH, SLOT_COUNT * PIXEL_PER_SLOT);

        LocalTime t = LocalTime.of(8, 0);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            g.drawRect(0, y, TIME_LABEL_WIDTH, PIXEL_PER_SLOT);
            g.drawString(t.toString(), 10, y + 20);
            t = t.plusMinutes(30);
        }

        // ─── 2) Draw each slot rectangle, and the patient name if booked ───
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            TimeSlot ts = currentSlots.get(i);

            // 2a) Fill background: gray if booked, white otherwise
            Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
            g.setColor(fill);
            g.fillRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            // 2b) Draw border
            g.setColor(Color.BLACK);
            g.drawRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            // 2c) If booked, draw the patient’s name inside
            if (ts.isBooked()) {
                g.setColor(Color.BLACK);
                String patient = ts.getPatientName();
                // Truncate if too long:
                String display = (patient.length() > 18)
                        ? patient.substring(0, 15) + "…"
                        : patient;
                g.drawString(display, TIME_LABEL_WIDTH + 5, y + 18);
            }
        }
    }
}
