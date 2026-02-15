package com.example.management.infrastructure.adapters.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {

    private final UUID orderId;
    private final UUID customerId;
    private final String status;
    private final List<OrderItemResponse> items;
    private final BigDecimal totalAmount;
    private final String currency;
}
