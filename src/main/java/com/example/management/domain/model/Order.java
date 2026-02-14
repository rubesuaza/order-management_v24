package com.example.management.domain.model;

import java.util.UUID;

/**
 * Domain entity representing an order.
 */
public record Order(UUID id, String status) {
}
