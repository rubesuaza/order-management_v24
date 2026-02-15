package com.example.management.infrastructure.adapters.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/** Immutable response DTO for an order line item. */
@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private final UUID productId;
    private final int quantity;
    private final BigDecimal unitPrice;
}
