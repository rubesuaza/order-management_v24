package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final Money TEN_USD = new Money(new BigDecimal("10.00"));
    private static final Money FIVE_USD = new Money(new BigDecimal("5.00"));

    @Test
    void shouldCreateOrderWithAtLeastOneItem() {
        var orderId = UUID.randomUUID();
        var productId = UUID.randomUUID();
        var item = new OrderItem(productId, 2, TEN_USD);
        var order = Order.create(orderId, CUSTOMER_ID, List.of(item));

        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount().amount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRejectOrderWithNoItems() {
        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), CUSTOMER_ID, List.of()))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("at least one");
    }

    @Test
    void totalAmountShouldBeSumOfLineTotals() {
        var item1 = new OrderItem(UUID.randomUUID(), 2, TEN_USD);
        var item2 = new OrderItem(UUID.randomUUID(), 1, FIVE_USD);
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID, List.of(item1, item2));

        assertThat(order.getTotalAmount().amount()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void shouldNotPlaceOrderBelowMinimumValue() {
        var item = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("9.99")));
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID, List.of(item));

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("10");
    }

    @Test
    void shouldPlaceOrderWhenTotalAtOrAboveMinimum() {
        var item = new OrderItem(UUID.randomUUID(), 1, TEN_USD);
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID, List.of(item));

        order.markAsPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldCancelOrderWhenPending() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldCancelOrderWhenPaid() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        order.markAsPaid();
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldNotCancelOrderWhenShipped() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        order.markAsPaid();
        order.ship();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("CANCELLED");
    }

    @Test
    void shouldShipOrderOnlyWhenPaid() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        order.markAsPaid();
        order.ship();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldNotShipOrderWhenPending() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));

        assertThatThrownBy(order::ship)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("SHIPPED");
    }

    @Test
    void shouldReconstituteOrderWithGivenStatus() {
        var orderId = UUID.randomUUID();
        var item = new OrderItem(UUID.randomUUID(), 1, TEN_USD);
        var total = new Money(new BigDecimal("10.00"));
        var createdAt = LocalDateTime.now().minusDays(1);

        var order = Order.reconstitute(orderId, CUSTOMER_ID, createdAt,
                List.of(item), total, OrderStatus.SHIPPED);

        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldDeliverOrderOnlyWhenShipped() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        order.markAsPaid();
        order.ship();
        order.deliver();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void shouldNotDeliverOrderWhenNotShipped() {
        var order = Order.create(UUID.randomUUID(), CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        order.markAsPaid();

        assertThatThrownBy(order::deliver)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("DELIVERED");
    }

    @Test
    void shouldRejectCreateOrderWhenOrderIdIsNull() {
        assertThatThrownBy(() -> Order.create(null, CUSTOMER_ID,
                List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD))))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectCreateOrderWhenItemsIsNull() {
        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), CUSTOMER_ID, null))
                .isInstanceOf(NullPointerException.class);
    }
}
