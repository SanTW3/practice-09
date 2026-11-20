package ru.edu.pr06;

public final class StockItem {
    private final String code;
    private final String name;
    private final Category category;
    private int quantity;

    public StockItem(String code, String name, Category category, int quantity) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is null or blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        this.code = code;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    public void increase(int q) {
        if (q <= 0) {
            throw new IllegalArgumentException("q must be > 0");
        }
        if (q > Integer.MAX_VALUE - quantity) {
            throw new IllegalArgumentException("quantity overflow");
        }
        quantity += q;
    }

    public void decrease(int q) {
        if (q <= 0) {
            throw new IllegalArgumentException("q must be > 0");
        }
        if (q > quantity) {
            throw new IllegalArgumentException("quantity cannot become negative");
        }
        quantity -= q;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getQuantity() { return quantity; }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof StockItem item && code.equals(item.code);
    }

    @Override
    public int hashCode() { return code.hashCode(); }

    @Override
    public String toString() {
        return code + " " + name + " " + category + " qty=" + quantity;
    }
}
