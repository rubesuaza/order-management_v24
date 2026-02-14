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
                .toList();
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

    /**
     * Updates an existing entity from the domain order, preserving existing item IDs.
     * Syncs status, totalAmount, currency and items (by index; new items get new IDs).
     */
    public void updateEntityFromDomain(OrderEntity entity, Order domain) {
        entity.setStatus(domain.getStatus().name());
        entity.setTotalAmount(domain.getTotalAmount().amount());
        entity.setCurrency(domain.getTotalAmount().currency());
        List<OrderItem> domainItems = domain.getItems();
        List<OrderItemEntity> entityItems = entity.getItems();
        int i = 0;
        for (; i < domainItems.size(); i++) {
            OrderItem di = domainItems.get(i);
            if (i < entityItems.size()) {
                OrderItemEntity ei = entityItems.get(i);
                ei.setProductId(di.productId());
                ei.setQuantity(di.quantity());
                ei.setUnitPrice(di.unitPrice().amount());
            } else {
                entityItems.add(new OrderItemEntity(
                        UUID.randomUUID(),
                        entity,
                        di.productId(),
                        di.quantity(),
                        di.unitPrice().amount()
                ));
            }
        }
        if (i < entityItems.size()) {
            entityItems.subList(i, entityItems.size()).clear();
        }
    }
}
