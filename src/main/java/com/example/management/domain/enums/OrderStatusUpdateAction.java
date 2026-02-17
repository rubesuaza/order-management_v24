package com.example.management.domain.enums;

/**
 * Domain actions for updating an order's status.
 * Core business concepts for order state transitions.
 */
public enum OrderStatusUpdateAction {
    MARK_AS_PAID,
    MARK_AS_SHIPPED,
    MARK_AS_DELIVERED,
    CANCEL
}
