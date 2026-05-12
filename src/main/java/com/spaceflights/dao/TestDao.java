package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestDao {

    private static final String BASE_SELECT =
            "SELECT id, topic FROM tests";

    public List<Test> findAll() throws SQLException {
        List<Test> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Test> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Test> findByTopic(String topic) throws SQLException {
        String sql = BASE_SELECT + " WHERE topic = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, topic);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public int insert(Test test) throws SQLException {
        String sql = "INSERT INTO tests (topic) VALUES (?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, test.getTopic());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    test.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ теста");
        }
    }

    public boolean update(Test test) throws SQLException {
        String sql = "UPDATE tests SET topic = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, test.getTopic());
            ps.setInt(2, test.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tests WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Test mapRow(ResultSet rs) throws SQLException {
        return new Test(
                rs.getInt("id"),
                rs.getString("topic")
        );
    }
}
