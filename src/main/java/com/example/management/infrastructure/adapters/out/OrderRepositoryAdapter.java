package com.example.management.infrastructure.adapters.out;

import com.example.management.application.ports.out.OrderRepositoryPort;
import com.example.management.domain.model.Order;
import com.example.management.infrastructure.adapters.out.persistence.OrderJpaRepository;
import com.example.management.infrastructure.adapters.out.persistence.OrderPersistenceMapper;
import com.example.management.infrastructure.adapters.out.persistence.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Output adapter implementing order persistence via JPA.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        Optional<OrderEntity> existing = jpaRepository.findByIdWithItems(order.getId());
        if (existing.isPresent()) {
            OrderEntity entity = existing.get();
            mapper.updateEntityStatus(entity, order.getStatus());
            return mapper.toDomain(jpaRepository.save(entity));
        }
        OrderEntity entity = mapper.toEntity(order);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findByIdWithItems(orderId).map(mapper::toDomain);
    }
}
