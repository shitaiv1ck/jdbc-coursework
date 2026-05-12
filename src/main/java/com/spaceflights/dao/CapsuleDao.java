package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Capsule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CapsuleDao {

    private static final String BASE_SELECT =
            "SELECT id, id_ships, class FROM capsules";

    public List<Capsule> findAll() throws SQLException {
        List<Capsule> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Capsule> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Capsule> findByShip(int shipId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_ships = ? ORDER BY id";
        List<Capsule> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shipId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Capsule> findByClass(String capsuleClass) throws SQLException {
        String sql = BASE_SELECT + " WHERE class = ? ORDER BY id";
        List<Capsule> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, capsuleClass);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Capsule capsule) throws SQLException {
        String sql = "INSERT INTO capsules (id_ships, class) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, capsule.getIdShips());
            ps.setString(2, capsule.getCapsuleClass());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    capsule.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ капсулы");
        }
    }

    public boolean update(Capsule capsule) throws SQLException {
        String sql = "UPDATE capsules SET id_ships = ?, class = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, capsule.getIdShips());
            ps.setString(2, capsule.getCapsuleClass());
            ps.setInt(3, capsule.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM capsules WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Capsule mapRow(ResultSet rs) throws SQLException {
        return new Capsule(
                rs.getInt("id"),
                rs.getObject("id_ships", Integer.class),
                rs.getString("class")
        );
    }
}
