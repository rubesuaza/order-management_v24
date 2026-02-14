package com.example.management.infrastructure.adapters.in.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for order details (200).
 */
public record OrderDetailResponse(
        UUID orderId,
        UUID customerId,
        String status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String currency
) {}
