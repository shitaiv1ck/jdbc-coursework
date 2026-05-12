package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultDao {

    private static final String BASE_SELECT =
            "SELECT id_test, id_astrounaut, completed FROM results";

    public List<Result> findAll() throws SQLException {
        List<Result> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id_test, id_astrounaut")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Result> findById(int testId, int astronautId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_test = ? AND id_astrounaut = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, astronautId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Result> findByTest(int testId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_test = ? ORDER BY id_astrounaut";
        List<Result> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Result> findByAstronaut(int astronautId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_astrounaut = ? ORDER BY id_test";
        List<Result> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, astronautId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Result> findCompletedByTest(int testId, boolean completed) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_test = ? AND completed = ? ORDER BY id_astrounaut";
        List<Result> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setBoolean(2, completed);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public boolean insert(Result result) throws SQLException {
        String sql = "INSERT INTO results (id_test, id_astrounaut, completed) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, result.getIdTest());
            ps.setInt(2, result.getIdAstronaut());
            ps.setBoolean(3, result.getCompleted());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Result result) throws SQLException {
        String sql = "UPDATE results SET completed = ? WHERE id_test = ? AND id_astrounaut = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, result.getCompleted());
            ps.setInt(2, result.getIdTest());
            ps.setInt(3, result.getIdAstronaut());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int testId, int astronautId) throws SQLException {
        String sql = "DELETE FROM results WHERE id_test = ? AND id_astrounaut = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, astronautId);
            return ps.executeUpdate() > 0;
        }
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        return new Result(
                rs.getInt("id_test"),
                rs.getInt("id_astrounaut"),
                rs.getBoolean("completed")
        );
    }
}