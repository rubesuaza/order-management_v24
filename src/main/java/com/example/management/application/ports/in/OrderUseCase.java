package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port (use case interface) for order operations.
 */
public interface OrderUseCase {

    Order createOrder(UUID customerId, List<CreateOrderItemCommand> items);

    Optional<Order> getOrder(UUID orderId);

    Order payOrder(UUID orderId);
}
