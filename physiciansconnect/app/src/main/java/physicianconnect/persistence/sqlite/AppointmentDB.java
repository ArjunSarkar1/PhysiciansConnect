package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Appointment;
import physicianconnect.persistence.interfaces.AppointmentPersistence;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDB implements AppointmentPersistence {

    private final Connection connection;

    public AppointmentDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Appointment> getAppointmentsForPhysician(String physicianId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT patient_name, datetime, notes FROM appointments WHERE physician_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, physicianId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String patient = rs.getString("patient_name");
                String dateTime = rs.getString("datetime");
                String notes = rs.getString("notes");
                list.add(new Appointment(physicianId, patient, LocalDateTime.parse(dateTime), notes));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load appointments", e);
        }

        return list;
    }

    @Override
    public void addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (physician_id, patient_name, datetime, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, appointment.getPhysicianId());
            stmt.setString(2, appointment.getPatientName());
            stmt.setString(3, appointment.getDateTime().toString()); // ISO format
            stmt.setString(4, appointment.getNotes());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add appointment", e);
        }
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET notes = ? WHERE physician_id = ? AND patient_name = ? AND datetime = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, appointment.getNotes());
            stmt.setString(2, appointment.getPhysicianId());
            stmt.setString(3, appointment.getPatientName());
            stmt.setString(4, appointment.getDateTime().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update appointment", e);
        }
    }

    @Override
    public void deleteAppointment(Appointment appointment) {
        String sql = "DELETE FROM appointments WHERE physician_id = ? AND patient_name = ? AND datetime = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, appointment.getPhysicianId());
            stmt.setString(2, appointment.getPatientName());
            stmt.setString(3, appointment.getDateTime().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete appointment", e);
        }
    }

    @Override
    public void deleteAllAppointments() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM appointments");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all appointments", e);
        }
    }
}
