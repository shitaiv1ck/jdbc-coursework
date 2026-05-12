package com.spaceflights;

import com.spaceflights.dao.*;
import com.spaceflights.db.SchemaInitializer;
import com.spaceflights.model.*;
import com.spaceflights.service.BusinessQueryService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final FlightDao flightDao = new FlightDao();
    private static final SpaceshipDao shipDao = new SpaceshipDao();
    private static final FoodDao foodDao = new FoodDao();
    private static final CapsuleDao capsuleDao = new CapsuleDao();
    private static final AstronautDao astroDao = new AstronautDao();
    private static final TicketDao ticketDao = new TicketDao();
    private static final TestDao testDao = new TestDao();
    private static final ResultDao resultDao = new ResultDao();
    private static final BagDao bagDao = new BagDao();

    public static void main(String[] args) {
        try {
            SchemaInitializer.initialize();

            BusinessQueryService service = new BusinessQueryService();

            // Запускаем простые отчёты один за другим
            service.ticketsPerFlight();
            service.shipsWithFlights();
            service.astronautsInCapsules();
            service.testResultsSimple();
            service.astronautBags();

        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        while (true) {
            System.out.println("=== ДЕЙСТВИЕ ===");
            System.out.println("1. Создать\n2. Найти все\n3. Найти по ID\n4. Обновить\n5. Удалить\n0. Выход");
            int action = readInt("Выбор действия: ");
            if (action == 0) break;

            System.out.println("\n=== ТАБЛИЦА ===");
            System.out.println("1. Flights\n2. Spaceships\n3. Foods\n4. Capsules\n5. Astronauts\n6. Tickets\n7. Tests\n8. Results\n9. Bags");
            int table = readInt("Выбор таблицы: ");

            try {
                execute(action, table);
                System.out.println("Готово\n");
            } catch (SQLException e) {
                System.out.println("Ошибка БД: " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage() + "\n");
            }
        }
    }

    private static void execute(int action, int table) throws SQLException {
        switch (table) {
            case 1 -> handleFlight(action);
            case 2 -> handleSpaceship(action);
            case 3 -> handleFood(action);
            case 4 -> handleCapsule(action);
            case 5 -> handleAstronaut(action);
            case 6 -> handleTicket(action);
            case 7 -> handleTest(action);
            case 8 -> handleResult(action);
            case 9 -> handleBag(action);
            default -> System.out.println("⚠️ Неверный номер таблицы");
        }
    }

    // ================= FLIGHTS =================
    private static void handleFlight(int action) throws SQLException {
        switch (action) {
            case 1 -> flightDao.insert(new Flight(0, readStr("Назначение:"), null, null));
            case 2 -> flightDao.findAll().forEach(System.out::println);
            case 3 -> flightDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                flightDao.update(new Flight(id, readStr("Назначение:"), null, null));
            }
            case 5 -> flightDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= SPACESHIPS =================
    private static void handleSpaceship(int action) throws SQLException {
        switch (action) {
            case 1 -> shipDao.insert(new Spaceship(0, readInt("ID рейса:"), readInt("Кол-во капсул:")));
            case 2 -> shipDao.findAll().forEach(System.out::println);
            case 3 -> shipDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                shipDao.update(new Spaceship(id, readInt("ID рейса:"), readInt("Кол-во капсул:")));
            }
            case 5 -> shipDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= FOODS =================
    private static void handleFood(int action) throws SQLException {
        switch (action) {
            case 1 -> foodDao.insert(new Food(0, null, readStr("Состав:")));
            case 2 -> foodDao.findAll().forEach(System.out::println);
            case 3 -> foodDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                foodDao.update(new Food(id, null, readStr("Состав:")));
            }
            case 5 -> foodDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= CAPSULES =================
    private static void handleCapsule(int action) throws SQLException {
        switch (action) {
            case 1 -> capsuleDao.insert(new Capsule(0, null, readStr("Класс (эконом/панорамный люкс):")));
            case 2 -> capsuleDao.findAll().forEach(System.out::println);
            case 3 -> capsuleDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                capsuleDao.update(new Capsule(id, null, readStr("Класс:")));
            }
            case 5 -> capsuleDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= ASTRONAUTS =================
    private static void handleAstronaut(int action) throws SQLException {
        switch (action) {
            case 1 -> astroDao.insert(new Astronaut(0, null, readStr("Роль:"), readDec("Вес:"), readInt("Рост:")));
            case 2 -> astroDao.findAll().forEach(System.out::println);
            case 3 -> astroDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                astroDao.update(new Astronaut(id, null, readStr("Роль:"), readDec("Вес:"), readInt("Рост:")));
            }
            case 5 -> astroDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= TICKETS =================
    private static void handleTicket(int action) throws SQLException {
        switch (action) {
            case 1 -> ticketDao.insert(new Ticket(0, null, readInt("ID рейса:"), readDec("Цена:")));
            case 2 -> ticketDao.findAll().forEach(System.out::println);
            case 3 -> ticketDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                ticketDao.update(new Ticket(id, null, readInt("ID рейса:"), readDec("Цена:")));
            }
            case 5 -> ticketDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= TESTS =================
    private static void handleTest(int action) throws SQLException {
        switch (action) {
            case 1 -> testDao.insert(new Test(0, readStr("Название теста:")));
            case 2 -> testDao.findAll().forEach(System.out::println);
            case 3 -> testDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                testDao.update(new Test(id, readStr("Название теста:")));
            }
            case 5 -> testDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= RESULTS =================
    private static void handleResult(int action) throws SQLException {
        switch (action) {
            case 1 -> resultDao.insert(new Result(readInt("ID теста:"), readInt("ID астронавта:"), true));
            case 2 -> resultDao.findAll().forEach(System.out::println);
            case 3 -> {
                int tid = readInt("ID теста:"); int aid = readInt("ID астронавта:");
                resultDao.findById(tid, aid).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            }
            case 4 -> resultDao.update(new Result(readInt("ID теста:"), readInt("ID астронавта:"), true));
            case 5 -> resultDao.delete(readInt("ID теста:"), readInt("ID астронавта:"));
        }
    }

    // ================= BAGS =================
    private static void handleBag(int action) throws SQLException {
        switch (action) {
            case 1 -> bagDao.insert(new Bag(0, readInt("ID астронавта:"), readDec("Вес багажа:")));
            case 2 -> bagDao.findAll().forEach(System.out::println);
            case 3 -> bagDao.findById(readInt("ID:")).ifPresentOrElse(System.out::println, () -> System.out.println("Не найдено"));
            case 4 -> {
                int id = readInt("ID для обновления:");
                bagDao.update(new Bag(id, readInt("ID астронавта:"), readDec("Вес багажа:")));
            }
            case 5 -> bagDao.delete(readInt("ID для удаления:"));
        }
    }

    // ================= ВВОД =================
    private static int readInt(String msg) {
        System.out.print(msg + " ");
        while (!in.hasNextInt()) { System.out.print("Введите число: "); in.next(); }
        int val = in.nextInt(); in.nextLine(); return val;
    }

    private static String readStr(String msg) {
        System.out.print(msg + " ");
        String val = in.nextLine().trim();
        return val.isEmpty() ? null : val;
    }

    private static BigDecimal readDec(String msg) {
        System.out.print(msg + " ");
        while (!in.hasNextBigDecimal()) { System.out.print("Введите число: "); in.next(); }
        BigDecimal val = in.nextBigDecimal(); in.nextLine(); return val;
    }
}

