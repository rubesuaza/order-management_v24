package com.example.management.domain.model;

import com.example.management.domain.exception.DomainException;
import com.example.management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Aggregate")
class OrderTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();

    private static OrderItem validItem() {
        return new OrderItem(PRODUCT_ID, 2, new Money(new BigDecimal("5.00")));
    }

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void createsWithAtLeastOneItemAndCalculatesTotal() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            assertThat(order.getId()).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(order.getItems()).hasSize(1);
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("10.00");
            assertThat(order.getCreatedAt()).isNotNull();
        }

        @Test
        void totalAmountIsSumOfLineTotals() {
            OrderItem a = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("3.00")));
            OrderItem b = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("4.00")));
            Order order = new Order(CUSTOMER_ID, List.of(a, b));
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("10.00");
        }

        @Test
        void rejectsNullCustomerId() {
            assertThatThrownBy(() -> new Order(null, List.of(validItem())))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("CustomerId");
        }

        @Test
        void rejectsEmptyItems() {
            assertThatThrownBy(() -> new Order(CUSTOMER_ID, List.of()))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("at least one");
        }
    }

    @Nested
    @DisplayName("mark as paid")
    class MarkAsPaid {
        @Test
        void paidWhenTotalAtLeast10USD() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            order.markAsPaid();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        void rejectsPaidWhenTotalBelowMinimum() {
            OrderItem cheap = new OrderItem(PRODUCT_ID, 1, new Money(new BigDecimal("5.00")));
            Order order = new Order(CUSTOMER_ID, List.of(cheap));
            assertThatThrownBy(order::markAsPaid)
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("10");
        }

        @Test
        void rejectsPaidWhenNotPending() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            order.markAsPaid();
            assertThatThrownBy(order::markAsPaid)
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }

    @Nested
    @DisplayName("ship")
    class Ship {
        @Test
        void shipWhenPaid() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            order.markAsPaid();
            order.ship();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        void rejectsShipWhenNotPaid() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            assertThatThrownBy(order::ship)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("PAID");
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {
        @Test
        void cancelWhenPending() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void cancelWhenPaid() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            order.markAsPaid();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void rejectsCancelWhenShipped() {
            Order order = new Order(CUSTOMER_ID, List.of(validItem()));
            order.markAsPaid();
            order.ship();
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("SHIPPED");
        }
    }
}
