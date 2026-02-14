package com.example.management.infrastructure.adapters.in;

import com.example.management.application.ports.in.CreateOrderItemCommand;
import com.example.management.application.ports.in.OrderUseCase;
import com.example.management.domain.model.Order;
import com.example.management.infrastructure.adapters.in.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


/**
 * Input adapter (REST controller) for order operations. Base path /api/v1.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<CreateOrderItemCommand> items = request.items().stream()
                .map(dto -> new CreateOrderItemCommand(dto.productId(), dto.quantity(), dto.unitPrice()))
                .toList();
        Order order = orderUseCase.createOrder(request.customerId(), items);
        CreateOrderResponse body = new CreateOrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount().amount(),
                order.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable UUID orderId) {
        return orderUseCase.getOrder(orderId)
                .map(order -> ResponseEntity.ok(toDetailResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PayOrderResponse> payOrder(@PathVariable UUID orderId) {
        Order order = orderUseCase.payOrder(orderId);
        return ResponseEntity.ok(new PayOrderResponse(order.getId(), order.getStatus().name()));
    }

    private static OrderDetailResponse toDetailResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.productId(),
                        item.quantity(),
                        item.unitPrice().amount()
                ))
                .toList();
        return new OrderDetailResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                items,
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency()
        );
    }
}
