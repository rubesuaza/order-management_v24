package com.example.management.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application-level output DTO for create order use case.
 */
@Getter
@AllArgsConstructor
public class CreateOrderOutput {

    private final UUID orderId;
    private final String status;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;
}
