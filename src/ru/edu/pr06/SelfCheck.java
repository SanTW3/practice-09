package ru.edu.pr06;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

public final class SelfCheck {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-19T12:00:00Z"), ZoneOffset.UTC);
    private static final LocalDate TODAY = LocalDate.now(CLOCK);
    private static int passed;
    private static int failed;

    private static StockItem item() {
        return new StockItem("NB-01", "Notebook", Category.COMPUTER, 12);
    }

    private static Warehouse stock() {
        Warehouse warehouse = new Warehouse(CLOCK);
        warehouse.addItem(item());
        return warehouse;
    }

    private static int quantity(Warehouse warehouse) {
        return warehouse.findByCategory(Category.COMPUTER).get(0).getQuantity();
    }

    private static void expect(boolean condition) {
        if (!condition) throw new AssertionError("unexpected result");
    }

    private static void throwsIAE(Runnable operation, String message) {
        try {
            operation.run();
        } catch (IllegalArgumentException e) {
            expect(e.getMessage() != null && e.getMessage().contains(message));
            return;
        }
        throw new AssertionError("IllegalArgumentException expected");
    }

    private static void check(String name, Runnable operation) {
        try {
            operation.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable e) {
            failed++;
            System.out.println("FAIL " + name + " — " + e);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1 && args[0].equals("--interactive")) {
            interactive();
            return;
        }
        unitChecks();
        dataChecks(args.length == 0 ? Path.of("data/sample.csv") : Path.of(args[0]));
        System.out.println("TOTAL PASS=" + passed + " FAIL=" + failed);
        if (failed > 0) System.exit(1);
    }

    private static void unitChecks() {
        // 1. StockItem constructor valid.
        check("unit/1 StockItem constructor valid", () -> {
            expect(item().getQuantity() == 12);
        });
        // 2. StockItem negative quantity.
        check("unit/2 StockItem negative quantity", () -> {
            throwsIAE(() -> new StockItem("X", "Y", Category.COMPUTER, -1), "quantity must be >= 0");
        });
        // 3. StockItem increase 5.
        check("unit/3 StockItem increase 5", () -> {
            StockItem i = item();
            i.increase(5);
            expect(i.getQuantity() == 17);
        });
        // 4. StockItem increase zero and negative.
        check("unit/4 StockItem increase zero and negative", () -> {
            throwsIAE(() -> item().increase(0), "q must be > 0");
            throwsIAE(() -> item().increase(-1), "q must be > 0");
        });
        // 5. StockItem decrease 5.
        check("unit/5 StockItem decrease 5", () -> {
            StockItem i = item();
            i.decrease(5);
            expect(i.getQuantity() == 7);
        });
        // 6. StockItem decrease exceeds stock.
        check("unit/6 StockItem decrease exceeds stock", () -> {
            StockItem i = item();
            throwsIAE(() -> i.decrease(13), "negative");
            expect(i.getQuantity() == 12);
        });
        // 7. Warehouse addItem valid.
        check("unit/7 Warehouse addItem valid", () -> {
            Warehouse w = stock();
            expect(w.itemsCount() == 1);
        });
        // 8. Warehouse duplicate code.
        check("unit/8 Warehouse duplicate code", () -> {
            Warehouse w = stock();
            throwsIAE(() -> w.addItem(item()), "code already exists");
            expect(w.itemsCount() == 1);
        });
        // 9. Warehouse addItem null.
        check("unit/9 Warehouse addItem null", () -> {
            throwsIAE(() -> stock().addItem(null), "item is null");
        });
        // 10. Warehouse receive valid.
        check("unit/10 Warehouse receive valid", () -> {
            Warehouse w = stock();
            w.receive("NB-01", 5);
            expect(quantity(w) == 17 && w.movementsCount() == 1);
        });
        // 11. Warehouse receive missing code.
        check("unit/11 Warehouse receive missing code", () -> {
            throwsIAE(() -> stock().receive("XX-99", 5), "code not found");
        });
        // 12. Warehouse issue valid.
        check("unit/12 Warehouse issue valid", () -> {
            Warehouse w = stock();
            w.issue("NB-01", 3);
            expect(quantity(w) == 9 && w.movementsCount() == 1);
        });
        // 13. Warehouse issue exceeds stock.
        check("unit/13 Warehouse issue exceeds stock", () -> {
            Warehouse w = stock();
            throwsIAE(() -> w.issue("NB-01", 100), "negative");
            expect(quantity(w) == 12 && w.movementsCount() == 0);
        });
        // 14. Warehouse issue missing code.
        check("unit/14 Warehouse issue missing code", () -> {
            throwsIAE(() -> stock().issue("XX-99", 3), "code not found");
        });
        // 15. Warehouse find two computers.
        check("unit/15 Warehouse find two computers", () -> {
            Warehouse w = stock();
            w.addItem(new StockItem("PC-02", "Desktop", Category.COMPUTER, 2));
            expect(w.findByCategory(Category.COMPUTER).size() == 2);
        });
        // 16. Warehouse find absent category.
        check("unit/16 Warehouse find absent category", () -> {
            expect(stock().findByCategory(Category.NETWORK).isEmpty());
        });
        // 17. Warehouse search defensive copy.
        check("unit/17 Warehouse search defensive copy", () -> {
            Warehouse w = stock();
            List<StockItem> found = w.findByCategory(Category.COMPUTER);
            found.get(0).decrease(12);
            found.clear();
            expect(w.itemsCount() == 1 && quantity(w) == 12);
        });
        // 18. Warehouse totals by category.
        check("unit/18 Warehouse totals by category", () -> {
            Warehouse w = stock();
            w.addItem(new StockItem("MN-01", "Monitor", Category.DISPLAY, 7));
            w.addItem(new StockItem("KB-01", "Keyboard", Category.PERIPHERAL, 20));
            expect(w.totalsByCategory().equals(Map.of(Category.COMPUTER, 12, Category.DISPLAY, 7, Category.PERIPHERAL, 20)));
        });
        // 19. Variant A receive dated.
        check("unit/19 Variant A receive dated", () -> {
            Warehouse w = stock();
            w.receive("NB-01", 2);
            StockMovement m = w.addMovementsByDate(w, TODAY).get(0);
            expect(m.date().equals(TODAY) && m.type() == MovementType.RECEIVE && m.quantity() == 2);
        });
        // 20. Variant A issue dated.
        check("unit/20 Variant A issue dated", () -> {
            Warehouse w = stock();
            w.issue("NB-01", 2);
            StockMovement m = w.addMovementsByDate(w, TODAY).get(0);
            expect(m.date().equals(TODAY) && m.type() == MovementType.ISSUE && m.quantity() == 2);
        });
        // 21. Variant B date with movements.
        check("unit/21 Variant B date with movements", () -> {
            Warehouse w = stock();
            w.receive("NB-01", 1);
            w.issue("NB-01", 1);
            expect(w.addMovementsByDate(w, TODAY).size() == 2);
        });
        // 22. Variant B absent date.
        check("unit/22 Variant B absent date", () -> {
            Warehouse w = stock();
            w.receive("NB-01", 1);
            expect(w.addMovementsByDate(w, TODAY.minusDays(1)).isEmpty());
        });
        // 23. StockItem code and name guards.
        check("unit/23 StockItem code and name guards", () -> {
            throwsIAE(() -> new StockItem(null, "X", Category.COMPUTER, 0), "code");
            throwsIAE(() -> new StockItem(" ", "X", Category.COMPUTER, 0), "code");
            throwsIAE(() -> new StockItem("X", null, Category.COMPUTER, 0), "name");
            throwsIAE(() -> new StockItem("X", " ", Category.COMPUTER, 0), "name");
        });
        // 24. Category guards.
        check("unit/24 Category guards", () -> {
            throwsIAE(() -> new StockItem("X", "X", null, 0), "category");
            throwsIAE(() -> Category.valueOf("UNKNOWN"), "");
        });
        // 25. StockItem zero and exact decrease.
        check("unit/25 StockItem zero and exact decrease", () -> {
            StockItem i = item();
            i.decrease(12);
            expect(i.getQuantity() == 0);
        });
        // 26. StockItem equality and Set.
        check("unit/26 StockItem equality and Set", () -> {
            StockItem a = item();
            StockItem b = new StockItem("NB-01", "Other", Category.NETWORK, 0);
            Set<StockItem> set = new HashSet<>();
            set.add(a);
            set.add(b);
            a.increase(2);
            expect(a.equals(b) && a.hashCode() == b.hashCode() && set.size() == 1 && set.contains(a));
        });
        // 27. Warehouse owns added item.
        check("unit/27 Warehouse owns added item", () -> {
            StockItem i = item();
            Warehouse w = new Warehouse(CLOCK);
            w.addItem(i);
            i.decrease(12);
            expect(quantity(w) == 12);
        });
        // 28. Failed quantity leaves no movement.
        check("unit/28 Failed quantity leaves no movement", () -> {
            Warehouse w = stock();
            throwsIAE(() -> w.receive("NB-01", 0), "invalid movement");
            throwsIAE(() -> w.issue("NB-01", -1), "invalid movement");
            expect(w.movementsCount() == 0 && quantity(w) == 12);
        });
        // 29. StockItem overflow prevented.
        check("unit/29 StockItem overflow prevented", () -> {
            StockItem i = new StockItem("X", "X", Category.NETWORK, Integer.MAX_VALUE);
            throwsIAE(() -> i.increase(1), "overflow");
            expect(i.getQuantity() == Integer.MAX_VALUE);
        });
        // 30. Variant B copied result and target warehouse.
        check("unit/30 Variant B copied result and target warehouse", () -> {
            Warehouse a = stock();
            Warehouse b = stock();
            b.issue("NB-01", 1);
            List<StockMovement> found = a.addMovementsByDate(b, TODAY);
            expect(found.size() == 1);
            found.clear();
            expect(b.movementsCount() == 1);
        });
        // 31. Variant B null arguments.
        check("unit/31 Variant B null arguments", () -> {
            Warehouse w = stock();
            throwsIAE(() -> w.addMovementsByDate(null, TODAY), "required");
            throwsIAE(() -> w.addMovementsByDate(w, null), "required");
        });
        // 32. Warehouse totals and categories are copies.
        check("unit/32 Warehouse totals and categories are copies", () -> {
            Warehouse w = stock();
            w.totalsByCategory().clear();
            w.categories().clear();
            expect(w.categories().equals(Set.of(Category.COMPUTER)) && quantity(w) == 12);
        });
        // 33. Variant dates change at midnight.
        check("unit/33 Variant dates change at midnight", () -> {
            Clock late = Clock.fixed(Instant.parse("2026-11-18T23:30:00Z"), ZoneOffset.UTC);
            Warehouse w = new Warehouse(Clock.offset(late, Duration.ofHours(1)));
            w.addItem(item());
            w.receive("NB-01", 1);
            expect(w.addMovementsByDate(w, LocalDate.of(2026, 11, 19)).size() == 1);
        });
    }

    private static String readRow(Warehouse warehouse, String[] fields) {
        try {
            if (fields.length != 4 && fields.length != 5 && fields.length != 8) {
                throw new IllegalArgumentException("field count");
            }
            for (String field : fields) {
                if (!field.equals(field.strip())) {
                    throw new IllegalArgumentException("surrounding spaces");
                }
            }
            Category category;
            try {
                category = Category.valueOf(fields[2]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("category not found");
            }
            int q;
            try {
                q = Integer.parseInt(fields[3]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("quantity not a number");
            }
            // Extended variant rows act on an existing position and verify its dated history.
            if (fields.length == 8) {
                LocalDate date = LocalDate.parse(fields[6]);
                int expectedCount = Integer.parseInt(fields[7]);
                switch (fields[5]) {
                    case "RECEIVE" -> warehouse.receive(fields[0], q);
                    case "ISSUE" -> warehouse.issue(fields[0], q);
                    case "QUERY" -> { }
                    default -> throw new IllegalArgumentException("unknown action");
                }
                if (warehouse.addMovementsByDate(warehouse, date).size() != expectedCount) {
                    return "MISMATCH:date movements";
                }
            } else {
                warehouse.addItem(new StockItem(fields[0], fields[1], category, q));
            }
            for (StockItem item : warehouse.findByCategory(category)) {
                if (item.getCode().equals(fields[0])) return "OK:qty=" + item.getQuantity();
            }
            return "MISMATCH:item not found";
        } catch (IllegalArgumentException e) {
            return "SKIP:" + e.getMessage();
        }
    }

    private static void dataChecks(Path path) throws IOException {
        Warehouse warehouse = new Warehouse(CLOCK);
        int line = 0;
        int startPassed = passed;
        int startFailed = failed;
        for (String row : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            line++;
            if (row.isBlank() || row.startsWith("#") || row.startsWith("code;")) continue;
            String[] fields = row.split(";", -1);
            String expected = fields.length >= 5 ? fields[4] : "OK:qty=" + (fields.length > 3 ? fields[3] : "");
            final String name = "data/" + line;
            check(name, () -> {
                String actual = readRow(warehouse, fields);
                if (!actual.equals(expected)) {
                    throw new AssertionError("expected " + expected + ", got " + actual);
                }
            });
        }
        System.out.println("Data-driven: PASS=" + (passed - startPassed) + " FAIL=" + (failed - startFailed));
    }

    private static void interactive() {
        Warehouse warehouse = new Warehouse();
        System.out.println("add code name category qty | receive code qty | issue code qty");
        System.out.println("find category | totals | dates yyyy-MM-dd | categories | exit");
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank() || line.equals("exit")) break;
            String[] args = line.strip().split("\\s+");
            try {
                switch (args[0]) {
                    case "add" -> {
                        requireLength(args, 5);
                        warehouse.addItem(new StockItem(args[1], args[2], Category.valueOf(args[3]), Integer.parseInt(args[4])));
                    }
                    case "receive", "issue" -> {
                        requireLength(args, 3);
                        int q = Integer.parseInt(args[2]);
                        if (args[0].equals("receive")) warehouse.receive(args[1], q);
                        else warehouse.issue(args[1], q);
                    }
                    case "find" -> {
                        requireLength(args, 2);
                        System.out.println(warehouse.findByCategory(Category.valueOf(args[1])));
                    }
                    case "totals" -> {
                        requireLength(args, 1);
                        System.out.println(warehouse.totalsByCategory());
                    }
                    case "dates" -> {
                        requireLength(args, 2);
                        System.out.println(warehouse.addMovementsByDate(warehouse, LocalDate.parse(args[1])));
                    }
                    case "categories" -> {
                        requireLength(args, 1);
                        System.out.println(warehouse.categories());
                    }
                    default -> throw new IllegalArgumentException("unknown command");
                }
                System.out.println("items=" + warehouse.itemsCount() + " movements=" + warehouse.movementsCount()
                        + " totals=" + warehouse.totalsByCategory());
            } catch (RuntimeException e) {
                System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private static void requireLength(String[] args, int length) {
        if (args.length != length) throw new IllegalArgumentException("wrong number of arguments");
    }
}
