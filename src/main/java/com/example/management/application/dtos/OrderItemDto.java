package com.example.management.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-level DTO for order line item input (input port).
 * Immutable to ensure data integrity.
 */
@Getter
@AllArgsConstructor
public class OrderItemDto {

    private final UUID productId;
    private final int quantity;
    private final BigDecimal unitPrice;
}
