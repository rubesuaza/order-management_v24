package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderEntity;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps between domain Order/OrderItem and JPA entities.
 */
@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity(
                domain.getId(),
                domain.getCustomerId(),
                domain.getStatus().name(),
                domain.getTotalAmount().amount(),
                domain.getTotalAmount().currency(),
                domain.getCreatedAt()
        );
        for (OrderItem item : domain.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    UUID.randomUUID(),
                    entity,
                    item.productId(),
                    item.quantity(),
                    item.unitPrice().amount()
            );
            entity.getItems().add(itemEntity);
        }
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(i -> new OrderItem(i.getProductId(), i.getQuantity(), new Money(i.getUnitPrice(), entity.getCurrency())))
                .collect(Collectors.toList());
        Money total = new Money(entity.getTotalAmount(), entity.getCurrency());
        return Order.reconstitute(
                entity.getId(),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                items,
                total,
                OrderStatus.valueOf(entity.getStatus())
        );
    }

    public void updateEntityStatus(OrderEntity entity, OrderStatus status) {
        entity.setStatus(status.name());
    }
}
