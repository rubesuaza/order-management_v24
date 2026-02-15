package com.example.management.application.services;

import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.application.ports.in.OrderItemCommand;
import com.example.management.application.ports.in.PayOrderUseCase;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service implementing order use cases.
 */
@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(UUID customerId, List<OrderItemCommand> items) {
        List<OrderItem> domainItems = items.stream()
                .map(cmd -> new OrderItem(
                        cmd.getProductId(),
                        cmd.getQuantity(),
                        new Money(cmd.getUnitPrice())))
                .collect(Collectors.toList());
        Order order = new Order(customerId, domainItems);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Order pay(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.markAsPaid();
        return orderRepository.save(order);
    }
}
