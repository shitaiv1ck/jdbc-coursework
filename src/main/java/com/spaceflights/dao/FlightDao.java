package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao {

    private static final String BASE_SELECT = "SELECT id, destination, flight_date, arrive_date FROM flights";

    public List<Flight> findAll() throws SQLException {
        List<Flight> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Flight> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Flight> findByDestination(String destination) throws SQLException {
        String sql = BASE_SELECT + " WHERE destination ILIKE ? ORDER BY flight_date DESC";
        List<Flight> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + destination + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (destination, flight_date, arrive_date) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, flight.getDestination());
            ps.setTimestamp(2, flight.getFlightDate() != null ? Timestamp.valueOf(flight.getFlightDate()) : null);
            ps.setTimestamp(3, flight.getArriveDate() != null ? Timestamp.valueOf(flight.getArriveDate()) : null);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    flight.setId(keys.getInt(1));
                    return flight.getId();
                }
            }
            throw new SQLException("Не удалось получить ID рейса");
        }
    }

    public boolean update(Flight flight) throws SQLException {
        String sql = "UPDATE flights SET destination = ?, flight_date = ?, arrive_date = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flight.getDestination());
            ps.setTimestamp(2, flight.getFlightDate() != null ? Timestamp.valueOf(flight.getFlightDate()) : null);
            ps.setTimestamp(3, flight.getArriveDate() != null ? Timestamp.valueOf(flight.getArriveDate()) : null);
            ps.setInt(4, flight.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM flights WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Flight mapRow(ResultSet rs) throws SQLException {
        Timestamp flightTs = rs.getTimestamp("flight_date");
        Timestamp arriveTs = rs.getTimestamp("arrive_date");
        return new Flight(
                rs.getInt("id"),
                rs.getString("destination"),
                flightTs != null ? flightTs.toLocalDateTime() : null,
                arriveTs != null ? arriveTs.toLocalDateTime() : null
        );
    }
}
