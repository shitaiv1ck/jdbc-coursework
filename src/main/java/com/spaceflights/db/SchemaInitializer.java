package com.spaceflights.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.stream.Collectors;

/**
 * Инициализация схемы БД и заполнение тестовыми данными для космической системы.
 */
public class SchemaInitializer {

    private static final Logger log = LoggerFactory.getLogger(SchemaInitializer.class);

    public static void initialize() throws SQLException {
        log.info("Инициализация схемы БД...");
        executeSqlFile("schema.sql");
        seedTestData();
        log.info("Схема БД создана и заполнена тестовыми данными");
    }

    private static void executeSqlFile(String fileName) throws SQLException {
        String sql;
        try (InputStream is = SchemaInitializer.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new RuntimeException("SQL-файл не найден: " + fileName);
            sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения SQL-файла: " + fileName, e);
        }

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("Выполнен SQL-файл: {}", fileName);
        }
    }

    private static void seedTestData() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                seedFlights(conn);
                seedSpaceships(conn);
                seedCapsules(conn);
                seedFoods(conn);
                seedAstronauts(conn);
                seedTickets(conn);
                seedTests(conn);
                seedResults(conn);
                seedBags(conn);

                conn.commit();
                log.info("Тестовые данные загружены");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static void seedFlights(Connection conn) throws SQLException {
        String sql = "INSERT INTO flights (destination, flight_date, arrive_date) VALUES (?, ?, ?)";
        // Формат: "ГГГГ-ММ-ДД ЧЧ:ММ:СС" — подходит для Timestamp.valueOf()
        Object[][] flights = {
                {"Луна, База «Селена»", "2026-06-01 08:00:00", "2026-06-01 20:00:00"},
                {"Марс, Колония «Новый Мир»", "2026-07-15 10:00:00", "2026-10-20 14:00:00"},
                {"МКС, Орбитальная станция", "2026-05-20 06:30:00", "2026-05-20 12:45:00"},
                {"Венера, Исследовательский зонд", "2026-08-01 00:00:00", "2026-12-15 00:00:00"},
                {"Астероидный пояс, Шахта «Церера»", "2026-09-10 12:00:00", "2027-01-05 18:00:00"},
                {"Юпитер, Спутник «Европа»", "2026-11-01 09:00:00", "2027-06-30 15:00:00"},
                {"Сатурн, Кольца наблюдения", "2027-01-15 07:00:00", "2027-09-20 11:00:00"},
                {"Титан, База «Кассини»", "2027-03-01 05:00:00", "2027-11-10 09:00:00"},
                {"Плутон, Пост «Новые Горизонты»", "2027-05-01 00:00:00", "2028-08-15 00:00:00"},
                {"Проксима Центавра, Экспедиция «Звезда»", "2028-01-01 00:00:00", null}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] f : flights) {
                ps.setString(1, (String) f[0]);
                // Используем Timestamp.valueOf() — он принимает строку с пробелом
                ps.setTimestamp(2, f[1] != null ? Timestamp.valueOf((String) f[1]) : null);
                ps.setTimestamp(3, f[2] != null ? Timestamp.valueOf((String) f[2]) : null);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedSpaceships(Connection conn) throws SQLException {
        String sql = "INSERT INTO spaceships (id_flight, capsules_count) VALUES (?, ?)";
        Object[][] ships = {
                {1, 10}, {1, 8},
                {2, 20}, {2, 25}, {2, 18},
                {3, 6}, {3, 6},
                {4, 12},
                {5, 15}, {5, 15},
                {6, 30},
                {7, 28},
                {8, 35},
                {9, 40},
                {10, 50}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] s : ships) {
                ps.setInt(1, (Integer) s[0]);
                ps.setInt(2, (Integer) s[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedCapsules(Connection conn) throws SQLException {
        String sql = "INSERT INTO capsules (id_ships, class) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int capsuleId = 1;
            for (int shipId = 1; shipId <= 15; shipId++) {
                ps.setInt(1, shipId);
                ps.setString(2, capsuleId % 3 == 0 ? "панорамный люкс" : "эконом");
                ps.addBatch();
                capsuleId++;

                ps.setInt(1, shipId);
                ps.setString(2, capsuleId % 3 == 0 ? "панорамный люкс" : "эконом");
                ps.addBatch();
                capsuleId++;
            }
            ps.executeBatch();
        }
    }

    private static void seedFoods(Connection conn) throws SQLException {
        String sql = "INSERT INTO foods (id_ships, contents) VALUES (?, ?)";
        Object[][] foods = {
                {1, "Сублимированный борщ, протеиновые батончики, витаминный гель"},
                {1, "Кофе в капсулах, ореховая паста, фруктовые кубики"},
                {2, "Меню «Марс»: говядина по-строгановски, пюре, компот"},
                {2, "Энергетические батончики, изотонический напиток, сухофрукты"},
                {3, "Стандартный рацион МКС: курица, рис, овощи в вакуумной упаковке"},
                {4, "Долгосрочный запас: сублимированные блюда, мультивитамины, вода"},
                {5, "Рацион шахтёра: высококалорийные пасты, минеральные добавки"},
                {6, "Премиум-меню для дальних перелётов: икра, деликатесы, свежезамороженные продукты"},
                {7, "Научный рацион: нейтральные вкусы для экспериментов"},
                {8, "Колониальный набор: семена, стартовые культуры, базовые продукты"}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] f : foods) {
                ps.setInt(1, (Integer) f[0]);
                ps.setString(2, (String) f[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedAstronauts(Connection conn) throws SQLException {
        String sql = "INSERT INTO astrounauts (id_capsule, role, weight, height) VALUES (?, ?, ?, ?)";
        Object[][] astronauts = {
                {1, "командир", new BigDecimal("78.5"), 180},
                {1, "пилот", new BigDecimal("72.0"), 175},
                {1, "инженер", new BigDecimal("81.2"), 185},
                {1, "пассажир", new BigDecimal("65.0"), 168},
                {1, "пассажир", new BigDecimal("70.5"), 172},
                {2, "пассажир", new BigDecimal("58.3"), 162},
                {2, "пассажир", new BigDecimal("92.1"), 190},
                {2, "пассажир", new BigDecimal("67.8"), 170},
                {3, "учёный-биолог", new BigDecimal("63.4"), 165},
                {3, "учёный-физик", new BigDecimal("75.9"), 178},
                {3, "техник", new BigDecimal("69.2"), 173},
                {4, "пассажир", new BigDecimal("55.0"), 160},
                {4, "пассажир", new BigDecimal("88.7"), 195},
                {5, "командир", new BigDecimal("80.1"), 182},
                {5, "медик", new BigDecimal("66.5"), 169},
                {6, "пилот", new BigDecimal("74.3"), 177},
                {6, "инженер", new BigDecimal("79.8"), 184},
                {7, "пассажир", new BigDecimal("61.2"), 166},
                {7, "пассажир", new BigDecimal("85.4"), 188},
                {8, "учёный-геолог", new BigDecimal("71.6"), 176}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] a : astronauts) {
                ps.setObject(1, a[0]);
                ps.setString(2, (String) a[1]);
                ps.setBigDecimal(3, (BigDecimal) a[2]);
                ps.setInt(4, (Integer) a[3]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedTickets(Connection conn) throws SQLException {
        String sql = "INSERT INTO tickets (id_astrounaut, id_flight, price) VALUES (?, ?, ?)";
        Object[][] tickets = {
                {1, 1, new BigDecimal("150000.00")},
                {2, 1, new BigDecimal("150000.00")},
                {3, 1, new BigDecimal("150000.00")},
                {4, 1, new BigDecimal("500000.00")},
                {5, 1, new BigDecimal("500000.00")},
                {6, 2, new BigDecimal("2500000.00")},
                {7, 2, new BigDecimal("2500000.00")},
                {8, 2, new BigDecimal("2500000.00")},
                {9, 3, new BigDecimal("80000.00")},
                {10, 3, new BigDecimal("80000.00")},
                {11, 3, new BigDecimal("80000.00")},
                {12, 4, new BigDecimal("5000000.00")},
                {13, 4, new BigDecimal("5000000.00")},
                {14, 5, new BigDecimal("1200000.00")},
                {15, 5, new BigDecimal("1200000.00")},
                {16, 6, new BigDecimal("8000000.00")},
                {17, 6, new BigDecimal("8000000.00")},
                {18, 7, new BigDecimal("12000000.00")},
                {19, 7, new BigDecimal("12000000.00")},
                {20, 8, new BigDecimal("15000000.00")}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] t : tickets) {
                ps.setObject(1, t[0]);
                ps.setInt(2, (Integer) t[1]);
                ps.setBigDecimal(3, (BigDecimal) t[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedTests(Connection conn) throws SQLException {
        String sql = "INSERT INTO tests (topic) VALUES (?)";
        String[] tests = {
                "Физическая подготовка в невесомости",
                "Психологическая устойчивость",
                "Навигация в открытом космосе",
                "Экстренные ситуации: разгерметизация",
                "Работа с системами жизнеобеспечения",
                "Основы астрофизики",
                "Медицинская помощь в полёте",
                "Коммуникация с Землёй: протоколы",
                "Ремонт оборудования в скафандре",
                "Этика межпланетных миссий"
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String topic : tests) {
                ps.setString(1, topic);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedResults(Connection conn) throws SQLException {
        String sql = "INSERT INTO results (id_test, id_astrounaut, completed) VALUES (?, ?, ?)";
        Object[][] results = {
                {1, 1, true}, {2, 1, true}, {3, 1, true}, {4, 1, true}, {5, 1, true},
                {1, 2, true}, {2, 2, true}, {3, 2, false}, {6, 2, true},
                {1, 3, true}, {5, 3, true}, {9, 3, true},
                {2, 4, true}, {7, 4, false},
                {2, 5, true}, {8, 5, true},
                {1, 9, true}, {6, 9, true}, {10, 9, true},
                {1, 10, true}, {6, 10, true}, {3, 10, true},
                {5, 11, true}, {9, 11, true},
                {2, 12, false}, {7, 12, false},
                {1, 14, true}, {4, 14, true}, {5, 14, true},
                {7, 15, true}, {1, 15, true},
                {3, 16, true}, {4, 16, true}, {9, 16, true},
                {6, 17, true}, {10, 17, false},
                {2, 18, true},
                {1, 20, true}, {6, 20, true}, {3, 20, true}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] r : results) {
                ps.setInt(1, (Integer) r[0]);
                ps.setInt(2, (Integer) r[1]);
                ps.setBoolean(3, (Boolean) r[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedBags(Connection conn) throws SQLException {
        String sql = "INSERT INTO bags (id_astrounaut, flight_weight) VALUES (?, ?)";
        Object[][] bags = {
                {1, new BigDecimal("15.5")},
                {2, new BigDecimal("12.0")},
                {3, new BigDecimal("18.3")},
                {4, new BigDecimal("25.0")},
                {5, new BigDecimal("22.7")},
                {6, new BigDecimal("30.0")},
                {7, new BigDecimal("28.5")},
                {9, new BigDecimal("10.2")},
                {10, new BigDecimal("11.8")},
                {12, new BigDecimal("40.0")},
                {14, new BigDecimal("20.1")},
                {16, new BigDecimal("16.9")},
                {18, new BigDecimal("35.5")},
                {20, new BigDecimal("14.3")}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] b : bags) {
                ps.setInt(1, (Integer) b[0]);
                ps.setBigDecimal(2, (BigDecimal) b[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}





