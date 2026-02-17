package com.example.management.infrastructure.adapters.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Immutable DTO for order responses.
 * Uses String for status to decouple the external API from the domain OrderStatus enum.
 */
@Value
@Builder
@AllArgsConstructor
public class OrderResponse {
    UUID orderId;
    UUID customerId;
    String status;
    LocalDateTime createdAt;
    List<OrderItemResponse> items;
    BigDecimal totalAmount;
    String currency;
}
