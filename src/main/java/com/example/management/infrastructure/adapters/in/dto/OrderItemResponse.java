package com.example.management.infrastructure.adapters.in.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for an order line item.
 */
public record OrderItemResponse(
        UUID productId,
        int quantity,
        BigDecimal unitPrice
) {}
