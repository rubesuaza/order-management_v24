package com.example.management.infrastructure.adapters.in.rest;

import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.application.ports.in.OrderItemCommand;
import com.example.management.application.ports.in.PayOrderUseCase;
import com.example.management.domain.model.Order;
import com.example.management.infrastructure.adapters.in.rest.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for order API (base path /api/v1).
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
        List<OrderItemCommand> items = request.getItems().stream()
                .map(i -> new OrderItemCommand(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice()))
                .collect(Collectors.toList());
        Order order = createOrderUseCase.create(request.getCustomerId(), items);
        CreateOrderResponse response = new CreateOrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getCreatedAt()
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
        Order order = payOrderUseCase.pay(orderId);
        PayOrderResponse response = new PayOrderResponse(order.getId(), order.getStatus().name());
        return ResponseEntity.ok(response);
    }

    private OrderDetailResponse toOrderDetailResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice().getAmount()))
                .collect(Collectors.toList());
        return new OrderDetailResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                itemResponses,
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
    }
}
