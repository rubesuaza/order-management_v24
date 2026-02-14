package com.example.management.application.ports.out;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port (repository interface) for order persistence.
 */
public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);
}
