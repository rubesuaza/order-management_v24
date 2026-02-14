package com.example.management.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        var id = UUID.randomUUID();
        var order = new Order(id, "PENDING");
        assertThat(order.id()).isEqualTo(id);
        assertThat(order.status()).isEqualTo("PENDING");
    }
}
