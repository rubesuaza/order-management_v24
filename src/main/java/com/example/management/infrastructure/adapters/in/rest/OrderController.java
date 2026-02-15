package com.example.management.infrastructure.adapters.in.rest;

import com.example.management.application.dtos.OrderItemDto;
import com.example.management.application.dtos.OrderDetailOutput;
import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.application.ports.in.PayOrderUseCase;
import com.example.management.infrastructure.adapters.in.rest.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for order API (base path /api/v1).
 * Depends only on application-layer ports and DTOs, not on domain model.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderItemDto> items = request.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice()))
                .toList();
        var output = createOrderUseCase.create(request.getCustomerId(), items);
        CreateOrderResponse response = new CreateOrderResponse(
                output.getOrderId(),
                output.getStatus(),
                output.getTotalAmount(),
                output.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable UUID orderId) {
        return getOrderUseCase.getById(orderId)
                .map(this::toOrderDetailResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PayOrderResponse> payOrder(@PathVariable UUID orderId) {
        var output = payOrderUseCase.pay(orderId);
        PayOrderResponse response = new PayOrderResponse(output.getOrderId(), output.getStatus());
        return ResponseEntity.ok(response);
    }

    private OrderDetailResponse toOrderDetailResponse(OrderDetailOutput output) {
        List<OrderItemResponse> itemResponses = output.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice()))
                .toList();
        return new OrderDetailResponse(
                output.getOrderId(),
                output.getCustomerId(),
                output.getStatus(),
                itemResponses,
                output.getTotalAmount(),
                output.getCurrency()
        );
    }
}
