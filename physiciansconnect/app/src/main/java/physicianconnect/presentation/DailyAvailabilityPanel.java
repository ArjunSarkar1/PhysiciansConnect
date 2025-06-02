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
 */
public class DailyAvailabilityPanel extends JPanel {
    private final int physicianId;
    private final AvailabilityService availabilityService;
    private final AppointmentManager appointmentManager;
    private LocalDate currentDate;
    private List<TimeSlot> currentSlots;

    // Constants
    private static final int SLOT_COUNT = 16;          // 16 half‐hour slots 08:00–16:30
    private static final int PIXEL_PER_SLOT = 30;      // each row is 30px tall
    private static final int TIME_LABEL_WIDTH = 80;    // width of the left‐hand time column
    private static final int SLOT_COLUMN_WIDTH = 200;  // width of the slot column

    public DailyAvailabilityPanel(int physicianId,
                                  AvailabilityService svc,
                                  AppointmentManager apptMgr,
                                  LocalDate date) {
        this.physicianId = physicianId;
        this.availabilityService = svc;
        this.appointmentManager = apptMgr;
        this.currentDate = date;

        // Total width = time‐label column + slot column
        int totalWidth = TIME_LABEL_WIDTH + SLOT_COLUMN_WIDTH;
        // Total height = 16 slots × 30px
        int totalHeight = SLOT_COUNT * PIXEL_PER_SLOT;
        setPreferredSize(new Dimension(totalWidth + 1, totalHeight + 1));

        // Load initial slots for `date`
        loadSlotsForDate(date);

        // Mouse listener to handle clicks on free vs. booked slots
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // (1) If clicked in the time‐label column, ignore
                if (x < TIME_LABEL_WIDTH) {
                    return;
                }

                // (2) Compute which slot row (0..15) was clicked
                int slotIndex = y / PIXEL_PER_SLOT;
                if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
                    return;
                }

                TimeSlot ts = currentSlots.get(slotIndex);
                // The exact LocalDateTime of this slot:
                LocalDateTime slotTime = ts.getStart();

                if (!ts.isBooked()) {
                    // → FREE slot: confirm and open AddAppointmentDialog
                    int choice = JOptionPane.showConfirmDialog(
                            DailyAvailabilityPanel.this,
                            "Slot at " + slotTime.toLocalDate()
                                    + " " + slotTime.toLocalTime()
                                    + " is free.\nAdd an appointment?",
                            "Add Appointment",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        // Open AddAppointmentDialog, passing a callback that reloads this panel
                        AddAppointmentDialog addDlg = new AddAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(DailyAvailabilityPanel.this),
                                appointmentManager,
                                String.valueOf(physicianId),
                                () -> loadSlotsForDate(currentDate)   // callback runs after “Save”
                        );

                        // Pre‐fill dateSpinner/timeSpinner so it defaults to our clicked slotTime
                        java.util.Date prefill = java.util.Date.from(
                                slotTime.atZone(ZoneId.systemDefault()).toInstant()
                        );
                        addDlg.dateSpinner.setValue(prefill);
                        addDlg.timeSpinner.setValue(prefill);

                        addDlg.setVisible(true);
                    }
                }
                else {
                    // → BOOKED slot: find the matching Appointment by comparing date/time
                    Appointment existingAppt = null;
                    for (Appointment a : appointmentManager.getAppointmentsForPhysician(String.valueOf(physicianId))) {
                        if (a.getDateTime().equals(slotTime)) {
                            existingAppt = a;
                            break;
                        }
                    }

                    if (existingAppt != null) {
                        ViewAppointmentDialog viewDlg = new ViewAppointmentDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(DailyAvailabilityPanel.this),
                                appointmentManager,
                                existingAppt,
                                () -> loadSlotsForDate(currentDate)   // callback runs after “Update/Delete”
                        );
                        viewDlg.setVisible(true);
                    } else {
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
     * Loads 16 half‐hour slots for the given date. On SQLException,
     * falls back to “all free” using TimeSlot.generateDailySlots().
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

        // ─── (1) Draw the left‐hand time labels ───
        g.setColor(new Color(230, 230, 230));
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

        // ─── (2) Draw each slot rectangle **and** patient name if booked ───
        for (int i = 0; i < SLOT_COUNT; i++) {
            int y = i * PIXEL_PER_SLOT;
            TimeSlot ts = currentSlots.get(i);

            // 2a) fill background (gray if booked, white otherwise)
            Color fill = ts.isBooked() ? Color.LIGHT_GRAY : Color.WHITE;
            g.setColor(fill);
            g.fillRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            // 2b) draw border
            g.setColor(Color.BLACK);
            g.drawRect(TIME_LABEL_WIDTH, y, SLOT_COLUMN_WIDTH, PIXEL_PER_SLOT);

            // 2c) if booked, draw the patient’s name inside
            if (ts.isBooked()) {
                g.setColor(Color.BLACK);
                String patient = ts.getPatientName();
                // “…/2” so long names don’t overshoot; you can adjust or wrap as you wish
                String display = patient.length() > 18 ? patient.substring(0, 15) + "…" : patient;
                g.drawString(display, TIME_LABEL_WIDTH + 5, y + 18);
            }
        }
    }

}
