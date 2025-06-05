package physicianconnect.persistence.sqlite;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDB implements ReceptionistPersistence {
    private final Connection connection;

    public ReceptionistDB(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS receptionists (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    password TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create receptionists table", e);
        }
    }

    @Override
    public Receptionist getReceptionistById(String id) {
        String sql = "SELECT id, name, email, password FROM receptionists WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Receptionist(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find receptionist by id", e);
        }
        return null;
    }

    @Override
    public Receptionist getReceptionistByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM receptionists WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Receptionist(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find receptionist by email", e);
        }
        return null;
    }

    @Override
    public void addReceptionist(Receptionist receptionist) {
        String sql = "INSERT OR IGNORE INTO receptionists (id, name, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, receptionist.getId());
            stmt.setString(2, receptionist.getName());
            stmt.setString(3, receptionist.getEmail());
            stmt.setString(4, receptionist.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add receptionist", e);
        }
    }

    @Override
    public List<Receptionist> getAllReceptionists() {
        List<Receptionist> list = new ArrayList<>();
        String sql = "SELECT id, name, email, password FROM receptionists";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Receptionist(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch receptionists", e);
        }
        return list;
    }
}