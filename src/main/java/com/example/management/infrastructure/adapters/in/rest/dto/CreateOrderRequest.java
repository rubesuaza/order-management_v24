package com.example.management.infrastructure.adapters.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "customerId is required")
    private UUID customerId;

    @NotNull(message = "items are required")
    @Size(min = 1, message = "items must not be empty")
    @Valid
    private List<OrderItemRequest> items;
}
