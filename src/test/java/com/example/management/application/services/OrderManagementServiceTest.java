package com.example.management.application.services;

import com.example.management.application.ports.out.OrderRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderManagementServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private OrderManagementService orderManagementService;

    @Test
    void serviceShouldBeCreated() {
        assertThat(orderManagementService).isNotNull();
    }
}
