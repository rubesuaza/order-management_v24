package com.example.management.domain.model;

import com.example.management.domain.exception.DomainException;
import com.example.management.domain.exception.InvalidOrderStateException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final List<OrderItem> items;
    private final UUID customerId;

    private static final BigDecimal MINIMUM_ORDER_AMOUNT = new BigDecimal("10.00");

    public Order(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one OrderItem to be created");
        }
        
        this.orderId = UUID.randomUUID();
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs an Order from persisted data.
     * Public method for use by infrastructure adapters.
     */
    public static Order reconstruct(UUID orderId, UUID customerId, List<OrderItem> items, 
                            OrderStatus status, LocalDateTime createdAt) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one OrderItem");
        }
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
        
        Order order = new Order(customerId, items);
        try {
            java.lang.reflect.Field idField = Order.class.getDeclaredField("orderId");
            idField.setAccessible(true);
            idField.set(order, orderId);
            
            java.lang.reflect.Field statusField = Order.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(order, status);
            
            java.lang.reflect.Field createdAtField = Order.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, createdAt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstruct Order", e);
        }
        return order;
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
