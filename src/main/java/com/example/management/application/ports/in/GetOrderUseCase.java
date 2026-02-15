package com.example.management.application.ports.in;

import com.example.management.application.dtos.OrderDetailOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Input port: get order by id.
 */
public interface GetOrderUseCase {

    Optional<OrderDetailOutput> getById(UUID orderId);
}
