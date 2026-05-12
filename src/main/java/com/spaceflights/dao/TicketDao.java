package com.spaceflights.dao;

import com.spaceflights.db.ConnectionManager;
import com.spaceflights.model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;

public class TicketDao {

    private static final String BASE_SELECT =
            "SELECT id, id_astrounaut, id_flight, price FROM tickets";

    public List<Ticket> findAll() throws SQLException {
        List<Ticket> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT + " ORDER BY id")) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Ticket> findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Ticket> findByAstronaut(int astronautId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_astrounaut = ? ORDER BY id";
        List<Ticket> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, astronautId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public List<Ticket> findByFlight(int flightId) throws SQLException {
        String sql = BASE_SELECT + " WHERE id_flight = ? ORDER BY id";
        List<Ticket> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (id_astrounaut, id_flight, price) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, ticket.getIdAstronaut());
            ps.setInt(2, ticket.getIdFlight());
            ps.setBigDecimal(3, ticket.getPrice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    ticket.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить ключ билета");
        }
    }

    /**
     * Транзакционная покупка билета:
     * 1. Проверяем существование астронавта и рейса
     * 2. Создаём билет с ценой
     */
    public int purchaseTicket(Integer astronautId, int flightId, BigDecimal price) throws SQLException {
        String checkAstronaut = "SELECT COUNT(*) FROM astrounauts WHERE id = ?";
        String checkFlight = "SELECT COUNT(*) FROM flights WHERE id = ?";
        String insertSql = "INSERT INTO tickets (id_astrounaut, id_flight, price) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Проверяем астронавта
                try (PreparedStatement ps = conn.prepareStatement(checkAstronaut)) {
                    ps.setInt(1, astronautId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) == 0) {
                            conn.rollback();
                            throw new SQLException("Астронавт с id=" + astronautId + " не найден");
                        }
                    }
                }
                // Проверяем рейс
                try (PreparedStatement ps = conn.prepareStatement(checkFlight)) {
                    ps.setInt(1, flightId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) == 0) {
                            conn.rollback();
                            throw new SQLException("Рейс с id=" + flightId + " не найден");
                        }
                    }
                }
                // Создаём билет
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setObject(1, astronautId);
                    ps.setInt(2, flightId);
                    ps.setBigDecimal(3, price);
                    ps.executeUpdate();
                    conn.commit();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) return keys.getInt(1);
                    }
                    throw new SQLException("Не удалось получить ключ билета");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean update(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET id_astrounaut = ?, id_flight = ?, price = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, ticket.getIdAstronaut());
            ps.setInt(2, ticket.getIdFlight());
            ps.setBigDecimal(3, ticket.getPrice());
            ps.setInt(4, ticket.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tickets WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("id"),
                rs.getObject("id_astrounaut", Integer.class),
                rs.getInt("id_flight"),
                rs.getBigDecimal("price")
        );
    }
}
