package ru.edu.pr06;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Warehouse {
    private final Map<String, StockItem> items = new HashMap<>();
    private final List<StockMovement> movements = new ArrayList<>();
    private final Clock clock;

    public Warehouse() {
        this(Clock.systemDefaultZone());
    }

    public Warehouse(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("clock is null");
        }
        this.clock = clock;
    }

    public void addItem(StockItem item) {
        if (item == null) {
            throw new IllegalArgumentException("item is null");
        }
        if (items.containsKey(item.getCode())) {
            throw new IllegalArgumentException("code already exists");
        }
        items.put(item.getCode(), copy(item));
    }

    public void receive(String code, int q) {
        StockItem item = requireItem(code);
        StockMovement movement = new StockMovement(code, MovementType.RECEIVE, q, LocalDate.now(clock));
        item.increase(q);
        movements.add(movement);
    }

    public void issue(String code, int q) {
        StockItem item = requireItem(code);
        StockMovement movement = new StockMovement(code, MovementType.ISSUE, q, LocalDate.now(clock));
        item.decrease(q);
        movements.add(movement);
    }

    public List<StockItem> findByCategory(Category category) {
        List<StockItem> result = new ArrayList<>();
        for (StockItem item : items.values()) {
            if (item.getCategory() == category) {
                result.add(copy(item));
            }
        }
        return result;
    }

    public Map<Category, Integer> totalsByCategory() {
        Map<Category, Integer> result = new HashMap<>();
        for (StockItem item : items.values()) {
            Category category = item.getCategory();
            result.put(category, Math.addExact(result.getOrDefault(category, 0), item.getQuantity()));
        }
        return result;
    }

    public List<StockMovement> addMovementsByDate(Warehouse wh, LocalDate date) {
        if (wh == null || date == null) {
            throw new IllegalArgumentException("warehouse and date are required");
        }
        List<StockMovement> result = new ArrayList<>();
        for (StockMovement movement : wh.movements) {
            if (movement.date().equals(date)) {
                result.add(movement);
            }
        }
        return result;
    }

    public Set<Category> categories() {
        Set<Category> result = new HashSet<>();
        for (StockItem item : items.values()) {
            result.add(item.getCategory());
        }
        return result;
    }

    public int itemsCount() { return items.size(); }
    public int movementsCount() { return movements.size(); }

    private StockItem requireItem(String code) {
        StockItem item = items.get(code);
        if (item == null) {
            throw new IllegalArgumentException("code not found");
        }
        return item;
    }

    private static StockItem copy(StockItem item) {
        return new StockItem(item.getCode(), item.getName(), item.getCategory(), item.getQuantity());
    }
}
