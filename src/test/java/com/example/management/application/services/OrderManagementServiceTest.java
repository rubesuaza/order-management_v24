package com.example.management.application.services;

import com.example.management.application.ports.out.OrderRepositoryPort;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderManagementServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private OrderManagementService orderManagementService;

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final Money TEN_USD = new Money(new BigDecimal("10.00"));

    @Test
    void createOrderShouldDelegateToRepository() {
        var item = new OrderItem(UUID.randomUUID(), 2, TEN_USD);
        var savedOrder = Order.create(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderManagementService.createOrder(CUSTOMER_ID, List.of(item));

        assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getTotalAmount().amount()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void getOrderShouldReturnOptionalFromRepository() {
        var orderId = UUID.randomUUID();
        var order = Order.create(orderId, CUSTOMER_ID, List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        var result = orderManagementService.getOrder(orderId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(orderId);
    }

    @Test
    void getOrderWhenNotFoundShouldReturnEmpty() {
        when(orderRepositoryPort.findById(any(UUID.class))).thenReturn(Optional.empty());

        var result = orderManagementService.getOrder(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void payOrderShouldMarkAsPaidAndSave() {
        var orderId = UUID.randomUUID();
        var order = Order.create(orderId, CUSTOMER_ID, List.of(new OrderItem(UUID.randomUUID(), 1, TEN_USD)));
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderManagementService.payOrder(orderId);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepositoryPort).save(order);
    }

    @Test
    void payOrderWhenOrderNotFoundShouldThrow() {
        when(orderRepositoryPort.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderManagementService.payOrder(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order not found");
    }
}
