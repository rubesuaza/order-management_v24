package com.example.management.domain.model;

import com.example.management.domain.exception.DomainException;
import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidItems() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 2, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenOrderHasNoItems() {
        UUID customerId = UUID.randomUUID();
        List<OrderItem> emptyItems = new ArrayList<>();
        
        assertThatThrownBy(() -> new Order(customerId, emptyItems))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Order must have at least one item");
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        
        OrderItem item1 = new OrderItem(productId1, 2, new Money(new BigDecimal("10.00")));
        OrderItem item2 = new OrderItem(productId2, 3, new Money(new BigDecimal("5.50")));
        List<OrderItem> items = List.of(item1, item2);
        
        Order order = new Order(customerId, items);
        
        Money total = order.getTotalAmount();
        assertThat(total.getAmount()).isEqualByComparingTo(new BigDecimal("36.50"));
    }

    @Test
    void shouldThrowExceptionWhenPlacingOrderWithTotalLessThanMinimum() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("5.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        
        assertThatThrownBy(() -> order.markAsPaid())
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Order total must be at least 10.00 USD");
    }

    @Test
    void shouldAllowPlacingOrderWithTotalEqualToMinimum() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        order.markAsPaid();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldAllowCancellingPendingOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldAllowCancellingPaidOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        order.markAsPaid();
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowExceptionWhenCancellingShippedOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        order.markAsPaid();
        order.markAsShipped();
        
        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Cannot cancel order");
    }

    @Test
    void shouldAllowShippingPaidOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        order.markAsPaid();
        order.markAsShipped();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldThrowExceptionWhenShippingPendingOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        
        assertThatThrownBy(() -> order.markAsShipped())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Cannot ship order");
    }

    @Test
    void shouldAllowMarkingShippedOrderAsDelivered() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        List<OrderItem> items = List.of(item);
        
        Order order = new Order(customerId, items);
        order.markAsPaid();
        order.markAsShipped();
        order.markAsDelivered();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }
}
