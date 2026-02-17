package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrderPersistenceAdapter.
 * Tests the contract between the adapter and the database.
 */
@DataJpaTest
@Import({OrderPersistenceAdapter.class, com.example.management.infrastructure.adapters.out.persistence.mapper.OrderMapper.class})
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
class OrderPersistenceAdapterTest {

    @Autowired
    private OrderPersistenceAdapter adapter;

    private UUID customerId;
    private OrderItem item1;
    private OrderItem item2;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        item1 = new OrderItem(
                UUID.randomUUID(),
                2,
                new Money(new BigDecimal("15.50"))
        );
        item2 = new OrderItem(
                UUID.randomUUID(),
                1,
                new Money(new BigDecimal("25.00"))
        );
    }

    @Test
    void shouldSaveOrder() {
        // Given
        Order order = new Order(customerId, List.of(item1, item2));

        // When
        Order savedOrder = adapter.save(order);

        // Then
        assertThat(savedOrder.getOrderId()).isNotNull();
        assertThat(savedOrder.getCustomerId()).isEqualTo(customerId);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedOrder.getItems()).hasSize(2);
    }

    @Test
    void shouldFindOrderById() {
        // Given
        Order order = new Order(customerId, List.of(item1));
        Order savedOrder = adapter.save(order);

        // When
        Optional<Order> foundOrder = adapter.findById(savedOrder.getOrderId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getOrderId()).isEqualTo(savedOrder.getOrderId());
        assertThat(foundOrder.get().getCustomerId()).isEqualTo(customerId);
        assertThat(foundOrder.get().getItems()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Order> foundOrder = adapter.findById(nonExistentId);

        // Then
        assertThat(foundOrder).isEmpty();
    }

    @Test
    void shouldCheckIfOrderExists() {
        // Given
        Order order = new Order(customerId, List.of(item1));
        Order savedOrder = adapter.save(order);

        // When
        boolean exists = adapter.existsById(savedOrder.getOrderId());
        boolean notExists = adapter.existsById(UUID.randomUUID());

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldDeleteOrderById() {
        // Given
        Order order = new Order(customerId, List.of(item1));
        Order savedOrder = adapter.save(order);
        UUID orderId = savedOrder.getOrderId();

        // When
        adapter.deleteById(orderId);

        // Then
        assertThat(adapter.existsById(orderId)).isFalse();
        assertThat(adapter.findById(orderId)).isEmpty();
    }

    @Test
    void shouldUpdateOrderStatus() {
        // Given
        Order order = new Order(customerId, List.of(item1));
        Order savedOrder = adapter.save(order);
        savedOrder.markAsPaid();

        // When
        Order updatedOrder = adapter.save(savedOrder);

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updatedOrder.getOrderId()).isEqualTo(savedOrder.getOrderId());
    }

    @Test
    void shouldPreserveOrderItemsOnUpdate() {
        // Given
        Order order = new Order(customerId, List.of(item1, item2));
        Order savedOrder = adapter.save(order);
        savedOrder.markAsPaid();

        // When
        Order updatedOrder = adapter.save(savedOrder);

        // Then
        assertThat(updatedOrder.getItems()).hasSize(2);
        assertThat(updatedOrder.getTotalAmount().getAmount())
                .isEqualByComparingTo(new BigDecimal("56.00"));
    }
}
