package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Input port: create a new order.
 */
public interface CreateOrderUseCase {

    /**
     * @param customerId customer identifier
     * @param items      productId, quantity, unitPrice per item
     * @return the created order
     */
    Order create(UUID customerId, List<OrderItemCommand> items);
}
