package com.example.management.application.services;

import com.example.management.application.ports.in.OrderUseCase;
import com.example.management.application.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Use case implementation for order management.
 */
@Service
public class OrderManagementService implements OrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public OrderManagementService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }
}
