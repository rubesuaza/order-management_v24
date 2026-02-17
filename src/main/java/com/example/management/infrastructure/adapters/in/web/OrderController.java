package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.services.OrderService;
import com.example.management.domain.exception.DomainException;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.exception.OrderNotFoundException;
import com.example.management.domain.model.Order;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderResponse;
import com.example.management.infrastructure.adapters.in.web.mapper.OrderWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for order management operations.
 * Input adapter that handles HTTP requests and delegates to application services.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderWebMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = mapper.toDomain(request);
        Order createdOrder = orderService.createOrder(order);
        OrderResponse response = mapper.toResponse(createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> ResponseEntity.ok(mapper.toResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/mark-as-paid")
    public ResponseEntity<OrderResponse> markAsPaid(@PathVariable UUID orderId) {
        return handleOrderStatusUpdate(orderId, OrderService.OrderStatusUpdateAction.MARK_AS_PAID);
    }

    @PostMapping("/{orderId}/mark-as-shipped")
    public ResponseEntity<OrderResponse> markAsShipped(@PathVariable UUID orderId) {
        return handleOrderStatusUpdate(orderId, OrderService.OrderStatusUpdateAction.MARK_AS_SHIPPED);
    }

    @PostMapping("/{orderId}/mark-as-delivered")
    public ResponseEntity<OrderResponse> markAsDelivered(@PathVariable UUID orderId) {
        return handleOrderStatusUpdate(orderId, OrderService.OrderStatusUpdateAction.MARK_AS_DELIVERED);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        return handleOrderStatusUpdate(orderId, OrderService.OrderStatusUpdateAction.CANCEL);
    }

    private ResponseEntity<OrderResponse> handleOrderStatusUpdate(UUID orderId,
                                                                  OrderService.OrderStatusUpdateAction action) {
        try {
            Order order = orderService.updateOrderStatus(orderId, action);
            return ResponseEntity.ok(mapper.toResponse(order));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStateException | DomainException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
