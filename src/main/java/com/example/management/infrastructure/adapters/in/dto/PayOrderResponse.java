package com.example.management.infrastructure.adapters.in.dto;

import java.util.UUID;

/**
 * Response DTO for pay order (200).
 */
public record PayOrderResponse(
        UUID orderId,
        String status
) {}
