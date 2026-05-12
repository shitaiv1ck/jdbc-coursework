package com.spaceflights.service;

import com.spaceflights.db.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;

public class BusinessQueryService {

    // 1. 💰 Сколько билетов продано на каждый рейс
    // Простой запрос: рейс + билеты
    public void ticketsPerFlight() throws SQLException {
        System.out.println("=== Билеты по рейсам ===");
        String sql = """
                SELECT 
                    f.id AS flight_id,
                    f.destination,
                    f.flight_date,
                    COUNT(t.id) AS tickets_sold,
                    SUM(t.price) AS total_revenue
                FROM flights f
                LEFT JOIN tickets t ON f.id = t.id_flight
                GROUP BY f.id, f.destination, f.flight_date
                ORDER BY f.flight_date
                """;

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID | Направление | Дата | Билетов | Выручка");
            System.out.println("─".repeat(60));

            while (rs.next()) {
                System.out.printf("%2d | %-25s | %s | %5d | %10.2f%n",
                        rs.getInt("flight_id"),
                        rs.getString("destination"),
                        rs.getTimestamp("flight_date") != null
                                ? rs.getTimestamp("flight_date").toLocalDateTime().toLocalDate()
                                : "—",
                        rs.getInt("tickets_sold"),
                        rs.getBigDecimal("total_revenue") != null
                                ? rs.getBigDecimal("total_revenue")
                                : BigDecimal.ZERO);
            }
        }
        System.out.println();
    }

    // 2. 🚀 Корабли и их рейсы
    // Простой запрос: корабли + рейсы (2 таблицы)
    public void shipsWithFlights() throws SQLException {
        System.out.println("=== Корабли и их рейсы ===");
        String sql = """
                SELECT 
                    sp.id AS ship_id,
                    sp.capsules_count,
                    f.destination,
                    f.flight_date
                FROM spaceships sp
                JOIN flights f ON sp.id_flight = f.id
                ORDER BY f.flight_date, sp.id
                """;

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Корабль | Капсул | Направление | Дата вылета");
            System.out.println("─".repeat(55));

            while (rs.next()) {
                System.out.printf("%7d | %6d | %-25s | %s%n",
                        rs.getInt("ship_id"),
                        rs.getInt("capsules_count"),
                        rs.getString("destination"),
                        rs.getTimestamp("flight_date") != null
                                ? rs.getTimestamp("flight_date").toLocalDateTime().toLocalDate()
                                : "—");
            }
        }
        System.out.println();
    }

    // 3. 👨‍🚀 Астронавты и их капсулы
    // Простой запрос: астронавты + капсулы (2 таблицы)
    public void astronautsInCapsules() throws SQLException {
        System.out.println("=== Астронавты в капсулах ===");
        String sql = """
                SELECT 
                    a.id AS astronaut_id,
                    a.role,
                    a.weight,
                    a.height,
                    c.class AS capsule_class
                FROM astrounauts a
                LEFT JOIN capsules c ON a.id_capsule = c.id
                ORDER BY a.id
                """;

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID | Роль | Вес(кг) | Рост | Класс капсулы");
            System.out.println("─".repeat(50));

            while (rs.next()) {
                System.out.printf("%2d | %-12s | %6.1f | %4d | %s%n",
                        rs.getInt("astronaut_id"),
                        rs.getString("role"),
                        rs.getBigDecimal("weight"),
                        rs.getInt("height"),
                        rs.getString("capsule_class") != null
                                ? rs.getString("capsule_class")
                                : "—");
            }
        }
        System.out.println();
    }

    // 4. 📋 Результаты тестов: кто что сдал
    // Простой запрос: результаты + тесты + астронавты (3 таблицы — максимум)
    public void testResultsSimple() throws SQLException {
        System.out.println("=== Результаты тестов ===");
        String sql = """
                SELECT 
                    t.topic AS test_name,
                    a.role AS astronaut_role,
                    r.completed AS is_passed
                FROM results r
                JOIN tests t ON r.id_test = t.id
                JOIN astrounauts a ON r.id_astrounaut = a.id
                ORDER BY t.topic, a.id
                """;

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Тест | Роль астронавта | Статус");
            System.out.println("─".repeat(55));

            while (rs.next()) {
                String status = rs.getBoolean("is_passed") ? "Сдан" : "Не сдан";
                System.out.printf("%-30s | %-15s | %s%n",
                        rs.getString("test_name"),
                        rs.getString("astronaut_role"),
                        status);
            }
        }
        System.out.println();
    }

    // 5. 🎒 Багаж астронавтов
    // Простой запрос: багаж + астронавты (2 таблицы)
    public void astronautBags() throws SQLException {
        System.out.println("=== Багаж астронавтов ===");
        String sql = """
                SELECT 
                    a.id AS astronaut_id,
                    a.role,
                    b.flight_weight AS bag_weight
                FROM bags b
                JOIN astrounauts a ON b.id_astrounaut = a.id
                ORDER BY b.flight_weight DESC
                """;

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID | Роль | Вес багажа (кг)");
            System.out.println("─".repeat(40));

            while (rs.next()) {
                System.out.printf("%2d | %-12s | %6.2f%n",
                        rs.getInt("astronaut_id"),
                        rs.getString("role"),
                        rs.getBigDecimal("bag_weight") != null
                                ? rs.getBigDecimal("bag_weight")
                                : BigDecimal.ZERO);
            }
        }
        System.out.println();
    }
}