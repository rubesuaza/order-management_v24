package com.example.management.infrastructure.adapters.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {

    private final UUID orderId;
    private final String status;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;
}
