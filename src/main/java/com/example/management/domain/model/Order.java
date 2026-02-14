package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root representing a customer order.
 * Enforces: at least one item, total = sum(line totals), min 10 USD to place, valid state transitions.
 */
public final class Order {

    private static final BigDecimal MINIMUM_ORDER_AMOUNT = new BigDecimal("10.00");

    private final UUID id;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private OrderStatus status;

    private Order(UUID id, UUID customerId, List<OrderItem> items, Money totalAmount, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.totalAmount = totalAmount;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates an order with at least one item. Total is computed as sum(unitPrice * quantity) for all items.
     */
    public static Order create(UUID orderId, UUID customerId, List<OrderItem> items) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(items, "items must not be null");
        if (items.isEmpty()) {
            throw new InvalidItemException("An Order must have at least one OrderItem to be created");
        }
        Money total = items.stream()
                .map(OrderItem::lineTotal)
                .reduce(Money::add)
                .orElseThrow();
        return new Order(orderId, customerId, items, total, LocalDateTime.now());
    }

    /**
     * Reconstitutes an order from persistence (e.g. database) with a given status.
     */
    public static Order reconstitute(UUID id, UUID customerId, LocalDateTime createdAt,
                                    List<OrderItem> items, Money totalAmount, OrderStatus status) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(items, "items must not be null");
        Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Order order = new Order(id, customerId, items, totalAmount, createdAt);
        order.status = status;
        return order;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void markAsPaid() {
        if (!canBeMarkedAsPaid()) {
            if (status != OrderStatus.PENDING) {
                throw new InvalidOrderStateException("Order can only be marked as PAID when status is PENDING");
            }
            throw new InvalidOrderStateException(
                    "Order cannot be placed: total amount must be at least 10.00 USD");
        }
        this.status = OrderStatus.PAID;
    }

    private boolean canBeMarkedAsPaid() {
        return status == OrderStatus.PENDING && meetsMinimumOrderAmount();
    }

    private boolean meetsMinimumOrderAmount() {
        return totalAmount.amount().compareTo(MINIMUM_ORDER_AMOUNT) >= 0;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be SHIPPED when status is PAID");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Order can only be DELIVERED when status is SHIPPED");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status != OrderStatus.PENDING && status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                    "Order can only be CANCELLED when status is PENDING or PAID");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
