package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration/contract tests for OrderRepositoryAdapter.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({OrderRepositoryAdapter.class, com.example.management.infrastructure.adapters.out.persistence.mapper.OrderPersistenceMapper.class})
class OrderRepositoryAdapterTest {

    @Autowired
    OrderRepositoryAdapter adapter;

    @Test
    void save_newOrder_persistsAndReturnsOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
                new OrderItem(productId, 2, new Money(java.math.BigDecimal.valueOf(15.50)))
        );
        Order order = new Order(customerId, items);

        Order saved = adapter.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCustomerId()).isEqualTo(customerId);
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getTotalAmount().getAmount()).isEqualByComparingTo(java.math.BigDecimal.valueOf(31.00));

        Optional<Order> found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(found.get().getItems()).hasSize(1);
    }

    @Test
    void findById_existingOrder_returnsOrder() {
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
                new OrderItem(UUID.randomUUID(), 1, new Money(java.math.BigDecimal.TEN))
        ));
        Order saved = adapter.save(order);

        Optional<Order> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getCustomerId()).isEqualTo(customerId);
        assertThat(found.get().getItems()).hasSize(1);
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<Order> found = adapter.findById(UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    void save_afterMarkAsPaid_updatesStatus() {
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
                new OrderItem(UUID.randomUUID(), 2, new Money(java.math.BigDecimal.valueOf(10)))
        ));
        Order saved = adapter.save(order);
        saved.markAsPaid();

        Order updated = adapter.save(saved);

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
        Optional<Order> found = adapter.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.PAID);
    }
}
