package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.PhysicianPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhysicianDB implements PhysicianPersistence {

    private final Connection connection;

    public PhysicianDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addPhysician(Physician physician) {
        if (physician == null || physician.getId() == null || physician.getId().isBlank()) {
            throw new IllegalArgumentException("Physician ID cannot be null or blank.");
        }

        String sql = "INSERT OR IGNORE INTO physicians (id, name, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, physician.getId());
            stmt.setString(2, physician.getName());
            stmt.setString(3, physician.getEmail());
            stmt.setString(4, physician.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add physician", e);
        }
    }

    @Override
    public List<Physician> getAllPhysicians() {
        List<Physician> list = new ArrayList<>();
        String sql = "SELECT id, name, email, password FROM physicians";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Physician(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch physicians", e);
        }

        return list;
    }

    @Override
    public Physician getPhysicianById(String id) {
        String sql = "SELECT id, name, email, password FROM physicians WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Physician(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find physician", e);
        }
        return null;
    }

    @Override
    public void deletePhysicianById(String id) {
        String sql = "DELETE FROM physicians WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete physician", e);
        }
    }

    @Override
    public void deleteAllPhysicians() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM physicians");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all physicians", e);
        }
    }
}
