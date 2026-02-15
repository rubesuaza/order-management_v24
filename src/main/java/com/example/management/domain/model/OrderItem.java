package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import lombok.Getter;

import java.util.UUID;

/**
 * Order line item: product, quantity, and unit price.
 */
@Getter
public class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        if (productId == null || unitPrice == null) {
            throw new InvalidItemException("ProductId and unitPrice are required");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("quantity must be strictly greater than zero");
        }
        if (unitPrice.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("unit price cannot be negative");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Money getLineTotal() {
        return unitPrice.multiply(quantity);
    }
}
