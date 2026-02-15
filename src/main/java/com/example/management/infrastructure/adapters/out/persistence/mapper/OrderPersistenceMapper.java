package com.example.management.infrastructure.adapters.out.persistence.mapper;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderEntity;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Maps between domain Order/OrderItem and JPA entities.
 */
@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount().getAmount())
                .currency(order.getTotalAmount().getCurrency())
                .createdAt(order.getCreatedAt())
                .build();
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .id(item.getId() != null ? item.getId() : UUID.randomUUID())
                    .order(entity)
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice().getAmount())
                    .build();
            entity.addItem(itemEntity);
        }
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(i -> new OrderItem(
                        i.getId(),
                        i.getProductId(),
                        i.getQuantity(),
                        new Money(i.getUnitPrice(), entity.getCurrency())))
                .toList();
        Money totalAmount = new Money(entity.getTotalAmount(), entity.getCurrency());
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                items,
                totalAmount
        );
    }
}
