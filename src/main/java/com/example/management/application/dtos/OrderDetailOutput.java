package com.example.management.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Application-level output DTO for get order by id use case.
 */
@Getter
@AllArgsConstructor
public class OrderDetailOutput {

    private final UUID orderId;
    private final UUID customerId;
    private final String status;
    private final List<OrderItemOutput> items;
    private final BigDecimal totalAmount;
    private final String currency;
}
