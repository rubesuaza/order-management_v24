package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    private static final Money VALID_PRICE = new Money(new BigDecimal("10.00"));

    @Test
    void shouldCreateOrderItemWithValidQuantityAndPrice() {
        var productId = UUID.randomUUID();
        var item = new OrderItem(productId, 2, VALID_PRICE);
        assertThat(item.productId()).isEqualTo(productId);
        assertThat(item.quantity()).isEqualTo(2);
        assertThat(item.unitPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), 0, VALID_PRICE))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), -1, VALID_PRICE))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        var negativePrice = new Money(new BigDecimal("-5.00"));
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), 1, negativePrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("negative");
    }

    @Test
    void shouldAcceptZeroUnitPrice() {
        var zeroPrice = new Money(BigDecimal.ZERO);
        var item = new OrderItem(UUID.randomUUID(), 1, zeroPrice);
        assertThat(item.unitPrice().amount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void lineTotalShouldBeUnitPriceTimesQuantity() {
        var item = new OrderItem(UUID.randomUUID(), 3, new Money(new BigDecimal("4.00")));
        assertThat(item.lineTotal().amount()).isEqualByComparingTo(new BigDecimal("12.00"));
    }
}
