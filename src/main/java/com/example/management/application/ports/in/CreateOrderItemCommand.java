package com.example.management.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-level DTO for a single order line when creating an order.
 * Use case builds domain OrderItem/Money from this.
 */
public record CreateOrderItemCommand(UUID productId, int quantity, BigDecimal unitPrice) {}
