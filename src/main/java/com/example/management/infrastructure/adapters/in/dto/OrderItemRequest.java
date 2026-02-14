package com.example.management.infrastructure.adapters.in.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for an order line item.
 */
public record OrderItemRequest(
        @NotNull(message = "productId must not be null") UUID productId,
        @Positive(message = "quantity must be positive") int quantity,
        @NotNull(message = "unitPrice must not be null")
        @DecimalMin(value = "0", inclusive = false, message = "unitPrice must be positive") BigDecimal unitPrice
) {}
