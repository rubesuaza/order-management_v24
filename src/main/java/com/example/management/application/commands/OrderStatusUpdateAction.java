package com.example.management.application.commands;

/**
 * Command enum for updating an order's status.
 * Application-layer input for order state transitions (reusable, clear separation of concerns).
 */
public enum OrderStatusUpdateAction {
    MARK_AS_PAID,
    MARK_AS_SHIPPED,
    MARK_AS_DELIVERED,
    CANCEL
}
