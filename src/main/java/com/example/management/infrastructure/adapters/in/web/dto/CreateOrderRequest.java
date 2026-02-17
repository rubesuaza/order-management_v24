package com.example.management.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new order. Immutable.
 */
@Value
public class CreateOrderRequest {
    @NotNull(message = "Customer ID is required")
    UUID customerId;

    @NotEmpty(message = "Order must have at least one item")
    List<OrderItemRequest> items;
}
