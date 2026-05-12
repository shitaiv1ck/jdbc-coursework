package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Astronaut;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AstronautDao {

    private static final String BASE_SELECT =
            "SELECT id, id_capsule, role, weight, height FROM astrounauts";

    public List<Astronaut> findAll() throws SQLException {
        List<Astronaut> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Astronaut> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Astronaut> findByCapsule(int capsuleId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_capsule = ? ORDER BY id";
        List<Astronaut> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, capsuleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Astronaut> findByRole(String role) throws SQLException {
        String sql = BASE_SELECT + " WHERE role = ? ORDER BY id";
        List<Astronaut> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Astronaut astronaut) throws SQLException {
        String sql = "INSERT INTO astrounauts (id_capsule, role, weight, height) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, astronaut.getIdCapsule());
            ps.setString(2, astronaut.getRole() != null ? astronaut.getRole() : "пассажир");
            ps.setBigDecimal(3, astronaut.getWeight());
            ps.setInt(4, astronaut.getHeight());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    astronaut.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ астронавта");
        }
    }

    public boolean update(Astronaut astronaut) throws SQLException {
        String sql = "UPDATE astrounauts SET id_capsule = ?, role = ?, weight = ?, height = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, astronaut.getIdCapsule());
            ps.setString(2, astronaut.getRole());
            ps.setBigDecimal(3, astronaut.getWeight());
            ps.setInt(4, astronaut.getHeight());
            ps.setInt(5, astronaut.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM astrounauts WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Astronaut mapRow(ResultSet rs) throws SQLException {
        return new Astronaut(
                rs.getInt("id"),
                rs.getObject("id_capsule", Integer.class),
                rs.getString("role"),
                rs.getBigDecimal("weight"),
                rs.getInt("height")
        );
    }
}
