package com.example.management.domain.model;

import com.example.management.domain.exception.DomainException;
import com.example.management.domain.exception.InvalidOrderStateException;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root representing a customer order.
 * Manages the lifecycle and state transitions of an order.
 */
@Getter
public class Order {
    private final UUID orderId;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    @Getter(AccessLevel.NONE)
    private final List<OrderItem> items;
    private final UUID customerId;

    private static final BigDecimal MINIMUM_ORDER_AMOUNT = new BigDecimal("10.00");

    public Order(UUID customerId, List<OrderItem> items) {
        this(UUID.randomUUID(), customerId, items, OrderStatus.PENDING, LocalDateTime.now());
    }

    /**
     * Private constructor for reconstructing an Order from persisted data.
     * Used by infrastructure adapters via {@link #reconstruct}.
     */
    private Order(UUID orderId, UUID customerId, List<OrderItem> items,
                  OrderStatus status, LocalDateTime createdAt) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one OrderItem to be created");
        }
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Returns an unmodifiable view of the order items to preserve aggregate invariants.
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Reconstructs an Order from persisted data.
     * Public method for use by infrastructure adapters.
     */
    public static Order reconstruct(UUID orderId, UUID customerId, List<OrderItem> items,
                                   OrderStatus status, LocalDateTime createdAt) {
        return new Order(orderId, customerId, items, status, createdAt);
    }

    /**
     * Gets the total amount of the order (sum of all item subtotals).
     */
    public Money getTotalAmount() {
        Money total = new Money(BigDecimal.ZERO);
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    /**
     * Marks the order as PAID. Validates minimum order amount.
     */
    public void markAsPaid() {
        Money total = getTotalAmount();
        if (total.getAmount().compareTo(MINIMUM_ORDER_AMOUNT) < 0) {
            throw new DomainException(
                    String.format("Order total must be at least %.2f USD to be placed", 
                            MINIMUM_ORDER_AMOUNT.doubleValue())
            );
        }
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    String.format("Cannot mark order as PAID. Current status: %s", this.status)
            );
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * Marks the order as SHIPPED. Only allowed if order is PAID.
     */
    public void markAsShipped() {
        if (this.status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                    String.format("Cannot ship order. Order must be PAID, but current status is: %s", 
                            this.status)
            );
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * Marks the order as DELIVERED. Only allowed if order is SHIPPED.
     */
    public void markAsDelivered() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(
                    String.format("Cannot mark order as DELIVERED. Order must be SHIPPED, but current status is: %s", 
                            this.status)
            );
        }
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * Cancels the order. Only allowed if order is PENDING or PAID.
     */
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    String.format("Cannot cancel order. Order is already %s", this.status)
            );
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
