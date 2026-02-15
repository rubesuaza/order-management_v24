package com.example.management.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Application-level output DTO for pay order use case.
 */
@Getter
@AllArgsConstructor
public class PayOrderOutput {

    private final UUID orderId;
    private final String status;
}
