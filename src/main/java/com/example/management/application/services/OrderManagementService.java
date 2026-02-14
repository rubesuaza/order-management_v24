package com.example.management.application.services;

import com.example.management.application.ports.in.OrderUseCase;
import com.example.management.application.ports.out.OrderRepositoryPort;
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
    public Order createOrder(UUID customerId, List<OrderItem> items) {
        Order order = Order.create(UUID.randomUUID(), customerId, items);
        return orderRepositoryPort.save(order);
    }

    @Override
    public Optional<Order> getOrder(UUID orderId) {
        return orderRepositoryPort.findById(orderId);
    }

    @Override
    public Order payOrder(UUID orderId) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.markAsPaid();
        return orderRepositoryPort.save(order);
    }
}
