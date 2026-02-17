package com.example.management.infrastructure.adapters.in.web.mapper;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderItemRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderItemResponse;
import com.example.management.infrastructure.adapters.in.web.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper between web DTOs and domain models.
 */
@Component
public class OrderWebMapper {

    public Order toDomain(CreateOrderRequest request) {
        java.util.List<OrderItem> items = request.getItems().stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());

        return new Order(request.getCustomerId(), items);
    }

    public OrderResponse toResponse(Order order) {
        Money totalAmount = order.getTotalAmount();
        
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()))
                .totalAmount(totalAmount.getAmount())
                .currency(totalAmount.getCurrency())
                .build();
    }

    private OrderItem toDomainItem(OrderItemRequest request) {
        return new OrderItem(
                request.getProductId(),
                request.getQuantity(),
                new Money(request.getUnitPrice())
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        Money subtotal = item.calculateSubtotal();
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice().getAmount())
                .subtotal(subtotal.getAmount())
                .currency(item.getUnitPrice().getCurrency())
                .build();
    }
}
