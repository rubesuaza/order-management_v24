package com.example.management.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-level output DTO for an order line item in order detail.
 */
@Getter
@AllArgsConstructor
public class OrderItemOutput {

    private final UUID productId;
    private final int quantity;
    private final BigDecimal unitPrice;
}
