package com.example.management.infrastructure.adapters.out.persistence.mapper;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderEntity;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper between domain Order/OrderItem and persistence OrderEntity/OrderItemEntity.
 */
@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = OrderEntity.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .customerId(order.getCustomerId())
                .build();

        entity.setItems(order.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList()));

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        java.util.List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());

        return Order.reconstruct(
                entity.getOrderId(),
                entity.getCustomerId(),
                items,
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity order) {
        return OrderItemEntity.builder()
                .order(order)
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPriceAmount(item.getUnitPrice().getAmount())
                .unitPriceCurrency(item.getUnitPrice().getCurrency())
                .build();
    }

    private OrderItem toDomainItem(OrderItemEntity entity) {
        Money unitPrice = new Money(entity.getUnitPriceAmount(), entity.getUnitPriceCurrency());
        return new OrderItem(entity.getProductId(), entity.getQuantity(), unitPrice);
    }
}
