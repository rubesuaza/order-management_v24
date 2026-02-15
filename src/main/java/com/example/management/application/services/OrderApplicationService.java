package com.example.management.application.services;

import com.example.management.application.dtos.*;
import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.application.ports.in.PayOrderUseCase;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing order use cases.
 * Maps domain Order to application-level output DTOs.
 */
@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CreateOrderOutput create(UUID customerId, List<OrderItemDto> items) {
        List<OrderItem> domainItems = items.stream()
                .map(dto -> new OrderItem(
                        dto.getProductId(),
                        dto.getQuantity(),
                        new Money(dto.getUnitPrice())))
                .toList();
        Order order = new Order(customerId, domainItems);
        Order saved = orderRepository.save(order);
        return toCreateOrderOutput(saved);
    }

    @Override
    public Optional<OrderDetailOutput> getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(this::toOrderDetailOutput);
    }

    @Override
    public PayOrderOutput pay(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.markAsPaid();
        Order saved = orderRepository.save(order);
        return new PayOrderOutput(saved.getId(), saved.getStatus().name());
    }

    private CreateOrderOutput toCreateOrderOutput(Order order) {
        return new CreateOrderOutput(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getCreatedAt()
        );
    }

    private OrderDetailOutput toOrderDetailOutput(Order order) {
        List<OrderItemOutput> itemOutputs = order.getItems().stream()
                .map(i -> new OrderItemOutput(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice().getAmount()))
                .toList();
        return new OrderDetailOutput(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                itemOutputs,
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
    }
}
