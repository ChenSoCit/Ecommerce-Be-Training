package com.java.TrainningJV.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.TrainningJV.common.OrderStatus;
import com.java.TrainningJV.common.StatsRange;
import com.java.TrainningJV.dtos.request.OrderRequest;
import com.java.TrainningJV.dtos.response.OrderStatsResponse;
import com.java.TrainningJV.models.Order;
import com.java.TrainningJV.services.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Order testOrder;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testOrder = Order.builder()
                .id(1)
                .userId(1)
                .fullName("Test User")
                .email("test@example.com")
                .phone("0987654321")
                .address("Test Address")
                .status(OrderStatus.pending)
                .orderDate(LocalDateTime.now())
                .totalMoney(BigDecimal.valueOf(1000))
                .build();

        orderRequest = OrderRequest.builder()
                .userId(1)
                .fullName("Test User")
                .email("test@example.com")
                .phone("0987654321")
                .address("Test Address")
                .build();
    }

    @Test
    void getOrderById_Success() throws Exception {
        when(orderService.findOrderById(1)).thenReturn(testOrder);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get order sucessfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(orderService).findOrderById(1);
    }

    @Test
    void findAllOrder_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get order sucessfully"));

        verify(orderService).getOrders();
    }

    @Test
    void statisOrder_Success() throws Exception {
        OrderStatsResponse statsResponse = new OrderStatsResponse();
        when(orderService.staticsOrder(eq(1), any(StatsRange.class), any(), any())).thenReturn(statsResponse);

        mockMvc.perform(get("/api/v1/orders/stats/1")
                        .param("range", "WEEK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("order statistics successfull"));

        verify(orderService).staticsOrder(eq(1), any(StatsRange.class), any(), any());
    }

    @Test
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("create order successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(orderService).createOrder(any(OrderRequest.class));
    }

    @Test
    void createOrderFromCart_Success() throws Exception {
        when(orderService.createOrderFormCart(1)).thenReturn(testOrder);

        mockMvc.perform(post("/api/v1/orders/1/order-cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("create order form cart successfull"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(orderService).createOrderFormCart(1);
    }

    @Test
    void getOrderByUserId_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByUserId(1)).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/oders-userid/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get order by id successfully"));

        verify(orderService).getOrdersByUserId(1);
    }

    @Test
    void getOrderByDay_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.findOrderByDay()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/order-by/day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("arrange order total money by day"));

        verify(orderService).findOrderByDay();
    }

    @Test
    void getOrderByWeek_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.findOrderByWeek()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/order-by/week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("arrange order total money by week"));

        verify(orderService).findOrderByWeek();
    }

    @Test
    void getOrderByMonth_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.findOrderByMonth()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/order-by/month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("arrange order total money by month"));

        verify(orderService).findOrderByMonth();
    }

    @Test
    void getOrderByYear_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.findOrderByYear()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/order-by/year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("arrange order total money by year"));

        verify(orderService).findOrderByYear();
    }

    @Test
    void getOrderByArrange_Success() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<Order> orders = Arrays.asList(testOrder);
        
        when(orderService.findOrdersByRange(startDate, endDate)).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/arrange")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("arrange order total money by arrange"));

        verify(orderService).findOrdersByRange(startDate, endDate);
    }

    @Test
    void updateOrder_Success() throws Exception {
        when(orderService.updateOrder(anyInt(), any(OrderRequest.class))).thenReturn(testOrder);

        mockMvc.perform(put("/api/v1/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("update order successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(orderService).updateOrder(anyInt(), any(OrderRequest.class));
    }

    @Test
    void deleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(1);

        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("delete order successfully"))
                .andExpect(jsonPath("$.data").value(1));

        verify(orderService).deleteOrder(1);
    }
}
