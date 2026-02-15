package com.example.management.application.services;

import com.example.management.application.dtos.OrderItemDto;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@DisplayName("OrderApplicationService")
@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        void createsOrderAndSavesViaRepository() {
            OrderItemDto dto = new OrderItemDto(PRODUCT_ID, 2, new BigDecimal("10.00"));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            var result = orderApplicationService.create(CUSTOMER_ID, List.of(dto));

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            Order captured = captor.getValue();
            assertThat(captured.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(captured.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(captured.getItems()).hasSize(1);
            assertThat(captured.getItems().get(0).getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(captured.getItems().get(0).getQuantity()).isEqualTo(2);
            assertThat(captured.getTotalAmount().getAmount()).isEqualByComparingTo("20.00");
            assertThat(result).isNotNull();
            assertThat(result.getOrderId()).isEqualTo(captured.getId());
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING.name());
            assertThat(result.getTotalAmount()).isEqualByComparingTo("20.00");
        }

        @Test
        void createWithMultipleItems_calculatesTotalCorrectly() {
            List<OrderItemDto> dtos = List.of(
                    new OrderItemDto(PRODUCT_ID, 2, new BigDecimal("5.00")),
                    new OrderItemDto(UUID.randomUUID(), 1, new BigDecimal("10.00"))
            );
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            var output = orderApplicationService.create(CUSTOMER_ID, dtos);

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            assertThat(captor.getValue().getItems()).hasSize(2);
            assertThat(captor.getValue().getTotalAmount().getAmount()).isEqualByComparingTo("20.00");
            assertThat(output.getTotalAmount()).isEqualByComparingTo("20.00");
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {
        @Test
        void returnsOrderWhenFound() {
            UUID orderId = UUID.randomUUID();
            Order order = new Order(CUSTOMER_ID, List.of(
                    new OrderItem(PRODUCT_ID, 1, new Money(BigDecimal.TEN))
            ));
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            var result = orderApplicationService.getById(orderId);

            assertThat(result).isPresent();
            assertThat(result.get().getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.get().getOrderId()).isEqualTo(order.getId());
        }

        @Test
        void returnsEmptyWhenNotFound() {
            UUID orderId = UUID.randomUUID();
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            var result = orderApplicationService.getById(orderId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("pay")
    class Pay {
        @Test
        void marksOrderAsPaidAndSaves() {
            UUID orderId = UUID.randomUUID();
            Order order = new Order(CUSTOMER_ID, List.of(
                    new OrderItem(PRODUCT_ID, 2, new Money(new BigDecimal("10.00")))
            ));
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            var result = orderApplicationService.pay(orderId);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID.name());
            assertThat(result.getOrderId()).isEqualTo(orderId);
            verify(orderRepository).save(order);
        }

        @Test
        void throwsWhenOrderNotFound() {
            UUID orderId = UUID.randomUUID();
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderApplicationService.pay(orderId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }
}
