package com.example.management.application.services;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Application service for order operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public Order updateOrderStatus(UUID orderId, OrderStatusUpdateAction action) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        switch (action) {
            case MARK_AS_PAID -> order.markAsPaid();
            case MARK_AS_SHIPPED -> order.markAsShipped();
            case MARK_AS_DELIVERED -> order.markAsDelivered();
            case CANCEL -> order.cancel();
        }

        return orderRepository.save(order);
    }

    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    public enum OrderStatusUpdateAction {
        MARK_AS_PAID,
        MARK_AS_SHIPPED,
        MARK_AS_DELIVERED,
        CANCEL
    }
}
