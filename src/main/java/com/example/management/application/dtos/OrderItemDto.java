package com.example.management.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-level DTO for order line item input (input port).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private UUID productId;
    private int quantity;
    private BigDecimal unitPrice;
}
