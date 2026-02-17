package com.example.management.infrastructure.adapters.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for an order item in responses. Immutable.
 */
@Value
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    UUID productId;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
    String currency;
}
