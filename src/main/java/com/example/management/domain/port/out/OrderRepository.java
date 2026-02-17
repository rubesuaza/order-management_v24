package com.example.management.domain.port.out;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Order persistence operations.
 * Technology-agnostic contract defined in the domain layer (dependency inversion).
 */
public interface OrderRepository {
    /**
     * Saves an order.
     * @param order The order to save
     * @return The saved order
     */
    Order save(Order order);

    /**
     * Finds an order by its ID.
     * @param orderId The order ID
     * @return Optional containing the order if found, empty otherwise
     */
    Optional<Order> findById(UUID orderId);

    /**
     * Checks if an order exists by its ID.
     * @param orderId The order ID
     * @return true if the order exists, false otherwise
     */
    boolean existsById(UUID orderId);

    /**
     * Deletes an order by its ID.
     * @param orderId The order ID
     */
    void deleteById(UUID orderId);
}
