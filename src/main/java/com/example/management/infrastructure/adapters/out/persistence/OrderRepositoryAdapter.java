package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderEntity;
import com.example.management.infrastructure.adapters.out.persistence.mapper.OrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Output adapter: implements OrderRepository using JPA.
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        Optional<OrderEntity> existing = jpaRepository.findByIdWithItems(order.getId());
        if (existing.isPresent()) {
            OrderEntity entity = existing.get();
            entity.setStatus(order.getStatus().name());
            OrderEntity saved = jpaRepository.save(entity);
            return mapper.toDomain(saved);
        }
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findByIdWithItems(orderId)
                .map(mapper::toDomain);
    }
}
