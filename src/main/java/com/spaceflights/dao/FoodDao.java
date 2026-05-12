package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Food;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FoodDao {

    private static final String BASE_SELECT =
            "SELECT id, id_ships, contents FROM foods";

    public List<Food> findAll() throws SQLException {
        List<Food> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Food> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Food> findByShip(int shipId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_ships = ? ORDER BY id";
        List<Food> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shipId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Food food) throws SQLException {
        String sql = "INSERT INTO foods (id_ships, contents) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, food.getIdShips());
            ps.setString(2, food.getContents());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    food.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ еды");
        }
    }

    public boolean update(Food food) throws SQLException {
        String sql = "UPDATE foods SET id_ships = ?, contents = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, food.getIdShips());
            ps.setString(2, food.getContents());
            ps.setInt(3, food.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM foods WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Food mapRow(ResultSet rs) throws SQLException {
        return new Food(
                rs.getInt("id"),
                rs.getObject("id_ships", Integer.class),
                rs.getString("contents")
        );
    }
}
