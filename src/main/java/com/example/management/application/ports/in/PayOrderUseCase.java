package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.UUID;

/**
 * Input port: mark order as paid.
 */
public interface PayOrderUseCase {

    /**
     * @param orderId order to pay
     * @return the order after payment
     */
    Order pay(UUID orderId);
}
