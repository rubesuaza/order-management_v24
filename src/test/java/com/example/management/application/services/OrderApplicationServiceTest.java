package com.example.management.application.services;

import com.example.management.application.ports.in.OrderItemCommand;
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
            OrderItemCommand cmd = new OrderItemCommand(PRODUCT_ID, 2, new BigDecimal("10.00"));
            Order savedOrder = new Order(CUSTOMER_ID, List.of(
                    new OrderItem(PRODUCT_ID, 2, new Money(new BigDecimal("10.00")))
            ));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderApplicationService.create(CUSTOMER_ID, List.of(cmd));

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
        }

        @Test
        void createWithMultipleItems_calculatesTotalCorrectly() {
            List<OrderItemCommand> commands = List.of(
                    new OrderItemCommand(PRODUCT_ID, 2, new BigDecimal("5.00")),
                    new OrderItemCommand(UUID.randomUUID(), 1, new BigDecimal("10.00"))
            );
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            orderApplicationService.create(CUSTOMER_ID, commands);

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            assertThat(captor.getValue().getItems()).hasSize(2);
            assertThat(captor.getValue().getTotalAmount().getAmount()).isEqualByComparingTo("20.00");
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

            Optional<Order> result = orderApplicationService.getById(orderId);

            assertThat(result).isPresent();
            assertThat(result.get().getCustomerId()).isEqualTo(CUSTOMER_ID);
        }

        @Test
        void returnsEmptyWhenNotFound() {
            UUID orderId = UUID.randomUUID();
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            Optional<Order> result = orderApplicationService.getById(orderId);

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

            Order result = orderApplicationService.pay(orderId);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
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
