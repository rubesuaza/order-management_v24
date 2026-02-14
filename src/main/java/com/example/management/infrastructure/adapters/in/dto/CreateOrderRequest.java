package com.example.management.infrastructure.adapters.in.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating an order.
 */
public record CreateOrderRequest(
        @NotNull(message = "customerId must not be null") UUID customerId,
        @Valid @NotNull(message = "items must not be null") List<OrderItemRequest> items
) {}
