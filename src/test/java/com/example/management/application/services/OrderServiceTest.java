package com.example.management.application.services;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderService}.
 * Verifies orchestration logic and interactions with {@link OrderRepository}.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderUsingRepository() {
        // Arrange
        Order order = mock(Order.class);
        Order savedOrder = mock(Order.class);
        when(orderRepository.save(order)).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertThat(result).isEqualTo(savedOrder);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldReturnOrderWhenFoundById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.getOrderById(orderId);

        // Assert
        assertThat(result).contains(order);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFoundById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrderById(orderId);

        // Assert
        assertThat(result).isEmpty();
        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldApplyMarkAsPaidActionAndSaveOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.updateOrderStatus(
                orderId,
                OrderService.OrderStatusUpdateAction.MARK_AS_PAID
        );

        // Assert
        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(orderId);
        verify(order).markAsPaid();
        verify(orderRepository).save(order);
    }

    @Test
    void shouldApplyMarkAsShippedActionAndSaveOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.updateOrderStatus(
                orderId,
                OrderService.OrderStatusUpdateAction.MARK_AS_SHIPPED
        );

        // Assert
        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(orderId);
        verify(order).markAsShipped();
        verify(orderRepository).save(order);
    }

    @Test
    void shouldApplyMarkAsDeliveredActionAndSaveOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.updateOrderStatus(
                orderId,
                OrderService.OrderStatusUpdateAction.MARK_AS_DELIVERED
        );

        // Assert
        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(orderId);
        verify(order).markAsDelivered();
        verify(orderRepository).save(order);
    }

    @Test
    void shouldApplyCancelActionAndSaveOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.updateOrderStatus(
                orderId,
                OrderService.OrderStatusUpdateAction.CANCEL
        );

        // Assert
        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(orderId);
        verify(order).cancel();
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowWhenUpdatingStatusForNonExistingOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateOrderStatus(
                orderId,
                OrderService.OrderStatusUpdateAction.MARK_AS_PAID
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldDeleteOrderWhenExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(orderRepository).existsById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).existsById(orderId);
        verify(orderRepository, never()).deleteById(any());
    }
}

