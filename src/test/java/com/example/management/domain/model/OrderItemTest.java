package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemWithValidData() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        OrderItem item = new OrderItem(productId, 5, unitPrice);
        
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        assertThatThrownBy(() -> new OrderItem(productId, 0, unitPrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        assertThatThrownBy(() -> new OrderItem(productId, -1, unitPrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsNegative() {
        UUID productId = UUID.randomUUID();
        Money negativePrice = new Money(new BigDecimal("-10.00"));
        
        assertThatThrownBy(() -> new OrderItem(productId, 5, negativePrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("unit price cannot be negative");
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.50"));
        OrderItem item = new OrderItem(productId, 3, unitPrice);
        
        Money subtotal = item.calculateSubtotal();
        
        assertThat(subtotal.getAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
        assertThat(subtotal.getCurrency()).isEqualTo("USD");
    }
}
