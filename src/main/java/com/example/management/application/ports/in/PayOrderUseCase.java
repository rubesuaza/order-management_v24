package com.example.management.application.ports.in;

import com.example.management.application.dtos.PayOrderOutput;

import java.util.UUID;

/**
 * Input port: mark order as paid.
 */
public interface PayOrderUseCase {

    /**
     * @param orderId order to pay
     * @return application-level output DTO for the paid order
     */
    PayOrderOutput pay(UUID orderId);
}
