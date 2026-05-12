package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Bag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BagDao {

    private static final String BASE_SELECT =
            "SELECT id, id_astrounaut, flight_weight FROM bags";

    public List<Bag> findAll() throws SQLException {
        List<Bag> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Bag> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Bag> findByAstronaut(int astronautId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_astrounaut = ? ORDER BY id";
        List<Bag> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, astronautId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Bag bag) throws SQLException {
        String sql = "INSERT INTO bags (id_astrounaut, flight_weight) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bag.getIdAstronaut());
            ps.setBigDecimal(2, bag.getFlightWeight());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    bag.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ бага");
        }
    }

    public boolean update(Bag bag) throws SQLException {
        String sql = "UPDATE bags SET id_astrounaut = ?, flight_weight = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bag.getIdAstronaut());
            ps.setBigDecimal(2, bag.getFlightWeight());
            ps.setInt(3, bag.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM bags WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Bag mapRow(ResultSet rs) throws SQLException {
        return new Bag(
                rs.getInt("id"),
                rs.getInt("id_astrounaut"),
                rs.getBigDecimal("flight_weight")
        );
    }
}
