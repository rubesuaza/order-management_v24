package com.example.management.domain.model;

import com.example.management.domain.exception.DomainException;
import com.example.management.domain.exception.InvalidOrderStateException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root for the order lifecycle.
 * Identity: OrderId (UUID).
 */
@Getter
public class Order {

    private static final java.math.BigDecimal MINIMUM_ORDER_AMOUNT = new java.math.BigDecimal("10.00");

    private final UUID id;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final UUID customerId;
    private final Money totalAmount;

    public Order(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new DomainException("CustomerId is required");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one item");
        }
        this.id = UUID.randomUUID();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.customerId = customerId;
        this.totalAmount = calculateTotal(items);
    }

    /** Reconstitutes an order from persistence (e.g. database). */
    public Order(UUID id, UUID customerId, OrderStatus status, LocalDateTime createdAt,
                 List<OrderItem> items, Money totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalAmount = totalAmount != null ? totalAmount : new Money(java.math.BigDecimal.ZERO);
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private static Money calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(new Money(java.math.BigDecimal.ZERO), Money::add);
    }

    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only PENDING orders can be marked as PAID");
        }
        if (!meetsMinimumOrderAmount()) {
            throw new DomainException("Order total must be at least 10.00 USD to be placed");
        }
        this.status = OrderStatus.PAID;
    }

    private boolean meetsMinimumOrderAmount() {
        return totalAmount.getAmount().compareTo(MINIMUM_ORDER_AMOUNT) >= 0;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be SHIPPED when status is PAID");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Order cannot be CANCELLED when already SHIPPED or DELIVERED");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
