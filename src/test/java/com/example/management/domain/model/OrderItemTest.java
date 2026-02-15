package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderItem")
class OrderItemTest {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Money UNIT_PRICE = new Money(new BigDecimal("5.00"));

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void createsWithPositiveQuantityAndNonNegativePrice() {
            OrderItem item = new OrderItem(PRODUCT_ID, 2, UNIT_PRICE);
            assertThat(item.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(item.getQuantity()).isEqualTo(2);
            assertThat(item.getUnitPrice()).isEqualTo(UNIT_PRICE);
        }

        @Test
        void rejectsZeroQuantity() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 0, UNIT_PRICE))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        void rejectsNegativeQuantity() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, -1, UNIT_PRICE))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        void rejectsNegativeUnitPrice() {
            Money negativePrice = new Money(new BigDecimal("-1.00"));
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 1, negativePrice))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("price");
        }
    }

    @Nested
    @DisplayName("line total")
    class LineTotal {
        @Test
        void lineTotalIsUnitPriceTimesQuantity() {
            OrderItem item = new OrderItem(PRODUCT_ID, 3, new Money(new BigDecimal("2.50")));
            Money total = item.getLineTotal();
            assertThat(total.getAmount()).isEqualByComparingTo("7.50");
            assertThat(total.getCurrency()).isEqualTo("USD");
        }
    }
}
