package com.example.management.application.services;

import com.example.management.application.ports.in.CreateOrderItemCommand;
import com.example.management.application.ports.in.OrderUseCase;
import com.example.management.application.ports.out.OrderRepositoryPort;
import com.example.management.domain.exception.OrderNotFoundException;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case implementation for order management.
 */
@Service
public class OrderManagementService implements OrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public OrderManagementService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order createOrder(UUID customerId, List<CreateOrderItemCommand> items) {
        List<OrderItem> domainItems = items.stream()
                .map(cmd -> new OrderItem(cmd.productId(), cmd.quantity(), new Money(cmd.unitPrice())))
                .toList();
        Order order = Order.create(UUID.randomUUID(), customerId, domainItems);
        return orderRepositoryPort.save(order);
    }

    @Override
    public Optional<Order> getOrder(UUID orderId) {
        return orderRepositoryPort.findById(orderId);
    }

    @Override
    public Order payOrder(UUID orderId) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markAsPaid();
        return orderRepositoryPort.save(order);
    }
}
