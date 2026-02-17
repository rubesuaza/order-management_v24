package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.services.OrderService;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.application.commands.OrderStatusUpdateAction;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderItemRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderResponse;
import com.example.management.infrastructure.adapters.in.web.mapper.OrderWebMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Contract tests for OrderController.
 * Tests the HTTP contract and adapter behavior.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderWebMapper mapper;

    @Test
    void shouldCreateOrder() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(customerId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(2);
        itemRequest.setUnitPrice(new BigDecimal("15.50"));
        request.setItems(List.of(itemRequest));

        Order order = new Order(customerId, List.of(
                new OrderItem(productId, 2, new Money(new BigDecimal("15.50")))
        ));
        OrderResponse response = OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(customerId)
                .status("PENDING")
                .createdAt(null)
                .items(null)
                .totalAmount(null)
                .currency(null)
                .build();

        when(mapper.toDomain(any(CreateOrderRequest.class))).thenReturn(order);
        when(orderService.createOrder(any(Order.class))).thenReturn(order);
        when(mapper.toResponse(any(Order.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
                new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("10.00")))
        ));
        OrderResponse response = OrderResponse.builder()
                .orderId(orderId)
                .customerId(customerId)
                .status("PENDING")
                .createdAt(null)
                .items(null)
                .totalAmount(null)
                .currency(null)
                .build();

        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(order));
        when(mapper.toResponse(order)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));

        verify(orderService).getOrderById(orderId);
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(orderId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(orderId);
    }

    @Test
    void shouldMarkOrderAsPaid() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of(
                new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("15.50")))
        ));
        order.markAsPaid();
        OrderResponse response = OrderResponse.builder()
                .orderId(orderId)
                .customerId(null)
                .status("PAID")
                .createdAt(null)
                .items(null)
                .totalAmount(null)
                .currency(null)
                .build();

        when(orderService.updateOrderStatus(eq(orderId), eq(OrderStatusUpdateAction.MARK_AS_PAID)))
                .thenReturn(order);
        when(mapper.toResponse(order)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/orders/{orderId}/mark-as-paid", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(orderService).updateOrderStatus(orderId, OrderStatusUpdateAction.MARK_AS_PAID);
    }

    @Test
    void shouldCancelOrder() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of(
                new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("10.00")))
        ));
        order.cancel();
        OrderResponse response = OrderResponse.builder()
                .orderId(orderId)
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderService.updateOrderStatus(eq(orderId), eq(OrderStatusUpdateAction.CANCEL)))
                .thenReturn(order);
        when(mapper.toResponse(order)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(orderService).updateOrderStatus(orderId, OrderStatusUpdateAction.CANCEL);
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        doNothing().when(orderService).deleteOrder(orderId);

        // When & Then
        mockMvc.perform(delete("/api/orders/{orderId}", orderId))
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(orderId);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingOrderWithInvalidData() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        // Missing customerId and items

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }
}
