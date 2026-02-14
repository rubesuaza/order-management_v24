package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a line item within an order.
 * Identity is defined by the containing order and productId.
 */
public final class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (quantity <= 0) {
            throw new InvalidItemException("OrderItem quantity must be strictly greater than zero");
        }
        if (unitPrice.amount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("OrderItem unitPrice cannot be negative");
        }
        this.quantity = quantity;
    }

    public UUID productId() {
        return productId;
    }

    public int quantity() {
        return quantity;
    }

    public Money unitPrice() {
        return unitPrice;
    }

    public Money lineTotal() {
        return unitPrice.multiply(quantity);
    }
}
