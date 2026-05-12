package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Spaceship;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpaceshipDao {

    private static final String BASE_SELECT =
            "SELECT id, id_flight, capsules_count FROM spaceships";

    public List<Spaceship> findAll() throws SQLException {
        List<Spaceship> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Spaceship> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Spaceship> findByFlight(int flightId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_flight = ? ORDER BY id";
        List<Spaceship> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Spaceship spaceship) throws SQLException {
        String sql = "INSERT INTO spaceships (id_flight, capsules_count) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, spaceship.getIdFlight());
            ps.setInt(2, spaceship.getCapsulesCount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    spaceship.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ корабля");
        }
    }

    public boolean update(Spaceship spaceship) throws SQLException {
        String sql = "UPDATE spaceships SET id_flight = ?, capsules_count = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, spaceship.getIdFlight());
            ps.setInt(2, spaceship.getCapsulesCount());
            ps.setInt(3, spaceship.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM spaceships WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Spaceship mapRow(ResultSet rs) throws SQLException {
        return new Spaceship(
                rs.getInt("id"),
                rs.getInt("id_flight"),
                rs.getInt("capsules_count")
        );
    }
}
