package com.example.management.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an order is not found by ID.
 */
public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
