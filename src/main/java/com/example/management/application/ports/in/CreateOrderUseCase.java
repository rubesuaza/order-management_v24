package com.example.management.application.ports.in;

import com.example.management.application.dtos.CreateOrderOutput;
import com.example.management.application.dtos.OrderItemDto;

import java.util.List;
import java.util.UUID;

/**
 * Input port: create a new order.
 */
public interface CreateOrderUseCase {

    /**
     * @param customerId customer identifier
     * @param items      application DTOs: productId, quantity, unitPrice per item
     * @return the created order output (application-layer DTO)
     */
    CreateOrderOutput create(UUID customerId, List<OrderItemDto> items);
}
