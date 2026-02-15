package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.infrastructure.adapters.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for OrderEntity.
 */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
