package com.example.management.application.ports.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command DTO for an order line item (input port).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCommand {

    private UUID productId;
    private int quantity;
    private BigDecimal unitPrice;
}
