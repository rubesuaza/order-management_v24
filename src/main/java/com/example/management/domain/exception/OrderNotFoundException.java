package com.example.management.domain.exception;

import java.util.UUID;

/**
 * Thrown when an order is not found by id.
 */
public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
