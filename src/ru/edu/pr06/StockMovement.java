package ru.edu.pr06;

import java.time.LocalDate;

public record StockMovement(String itemCode, MovementType type, int quantity, LocalDate date) {
    public StockMovement {
        if (itemCode == null || itemCode.isBlank() || type == null || quantity <= 0 || date == null) {
            throw new IllegalArgumentException("invalid movement");
        }
    }
}
