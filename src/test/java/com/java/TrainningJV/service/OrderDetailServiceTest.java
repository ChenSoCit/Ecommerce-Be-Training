package com.java.TrainningJV.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.TrainningJV.common.OrderStatus;
import com.java.TrainningJV.dtos.request.OrderDetailRequest;
import com.java.TrainningJV.exceptions.BadRequestException;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.OrderDetailMapper;
import com.java.TrainningJV.mappers.mapper.OrderMapper;
import com.java.TrainningJV.mappers.mapper.ProductMapper;
import com.java.TrainningJV.mappers.mapperCustom.OrderDetailMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.OrderMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.ProductMapperCustom;
import com.java.TrainningJV.models.Order;
import com.java.TrainningJV.models.OrderDetails;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.services.impl.OrderDetailServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {
    @Mock private OrderDetailMapper orderDetailMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderMapperCustom orderMapperCustom;
    @Mock private ProductMapper productMapper;
    @Mock private ProductMapperCustom productMapperCustom;
    @Mock private OrderDetailMapperCustom orderDetailMapperCustom;

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    private Order orderTest;
    private Product productTest;
    private OrderDetailRequest orderDetailRequestTest;
    private OrderDetails orderDetailTest;

    @BeforeEach
    void setUp(){
        orderTest = Order.builder()
            .id(1)
            .userId(1)
            .fullName("test order")
            .status(OrderStatus.pending)  
            .email("emailtets@gamil.com")
            .phone("0123456789")
            .address("123 Test HN")
            .totalMoney(BigDecimal.valueOf(200.0))
            .orderDate(LocalDateTime.of(2025,8,21, 11,29))
        .build();

        productTest = Product.builder()
            .id(1)
            .name("product test")
            .description("description test")
            .price(BigDecimal.valueOf(100.0))
            .stockQuantity(10)
            .createdAt(LocalDateTime.of(2025, 8, 23, 12, 8))
            .updatedAt(LocalDateTime.of(2025, 8, 23, 12, 8))
            .categoryId(1)
        .build();

        orderDetailRequestTest = OrderDetailRequest.builder()
            .orderId(1)
            .productId(1)
            .numberOfProducts(2)
            .color("Red")
        .build();
        
        orderDetailTest = OrderDetails.builder()
            .id(1)
            .orderId(1)
            .productId(1)
            .price(BigDecimal.valueOf(100.0))
            .numberOfProducts(2)
            .totalMoney(BigDecimal.valueOf(200.0))
            .color("Red")
        .build();
    }

    @Test
    void insertOrderDetails_Success() {
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
        
        when(orderDetailMapper.insert(any(OrderDetails.class))).thenAnswer(invocation -> {
            OrderDetails orderDetail = invocation.getArgument(0);
            orderDetail.setId(1);
            return 1;
        });
        
        when(productMapperCustom.updateStock(1, 2)).thenReturn(1);
        when(orderMapperCustom.totalMoneyOrder(eq(1), any(BigDecimal.class))).thenReturn(1);

        OrderDetails result = orderDetailService.insertOrderDetails(orderDetailRequestTest);

        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals(1, result.getProductId());
        assertEquals(BigDecimal.valueOf(200.0), result.getTotalMoney());
        
        verify(orderDetailMapper).insert(any(OrderDetails.class));
        verify(productMapperCustom).updateStock(1, 2);
        verify(orderMapperCustom).totalMoneyOrder(eq(1), any(BigDecimal.class));
    }

    @Test
    void insertOrderDetails_InvalidQuantity() {
        OrderDetailRequest invalidRequest = OrderDetailRequest.builder()
            .orderId(1)
            .productId(1)
            .numberOfProducts(0)
            .color("Red")
        .build();

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderDetailService.insertOrderDetails(invalidRequest);
        });

        assertEquals("numberOfProducts must be > 0", ex.getMessage());
    }

    @Test
    void insertOrderDetails_OrderNotFound() {
        when(orderMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            OrderDetailRequest request = OrderDetailRequest.builder()
                .orderId(999)
                .productId(1)
                .numberOfProducts(2)
            .build();
            orderDetailService.insertOrderDetails(request);
        });

        assertEquals("Order not found with id: 999", ex.getMessage());
    }

    @Test
    void insertOrderDetails_OrderNotPending() {
        Order deliveredOrder = Order.builder()
            .id(1)
            .userId(1)
            .status(OrderStatus.delivered)
            .build();
        
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(deliveredOrder);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderDetailService.insertOrderDetails(orderDetailRequestTest);
        });

        assertEquals("Order status must be PENDING to add details", ex.getMessage());
    }

    @Test
    void insertOrderDetails_ProductNotFound() {
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(productMapper.selectByPrimaryKey(999)).thenReturn(null);

        OrderDetailRequest request = OrderDetailRequest.builder()
            .orderId(1)
            .productId(999)
            .numberOfProducts(2)
        .build();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            orderDetailService.insertOrderDetails(request);
        });

        assertEquals("Product not found with id: 999", ex.getMessage());
    }

    @Test
    void insertOrderDetails_InsertFailed() {
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
        when(orderDetailMapper.insert(any(OrderDetails.class))).thenReturn(0);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderDetailService.insertOrderDetails(orderDetailRequestTest);
        });

        assertEquals("Insert Order Details failed", ex.getMessage());
    }

    @Test
    void insertOrderDetails_NotEnoughStock() {
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
        when(orderDetailMapper.insert(any(OrderDetails.class))).thenReturn(1);
        when(productMapperCustom.updateStock(1, 2)).thenReturn(0);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderDetailService.insertOrderDetails(orderDetailRequestTest);
        });

        assertEquals("Not enough stock for productId=1", ex.getMessage());
    }

    @Test
    void selectOrderDetailsByOrderId_Success() {
        when(orderDetailMapper.selectByPrimaryKey(1)).thenReturn(orderDetailTest);

        OrderDetails result = orderDetailService.selectOrderDetailsByOrderId(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(orderDetailMapper, times(2)).selectByPrimaryKey(1);
    }

    @Test
    void selectOrderDetailsByOrderId_NotFound() {
        when(orderDetailMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            orderDetailService.selectOrderDetailsByOrderId(999);
        });

        assertEquals("Order not found with id: 999", ex.getMessage());
    }

    @Test
    void selectAllOrderDetails_Success() {
        List<OrderDetails> mockOrderDetails = Arrays.asList(orderDetailTest);
        when(orderDetailMapperCustom.selectAllOrderDetailsByOrderId(1)).thenReturn(mockOrderDetails);

        List<OrderDetails> result = orderDetailService.selectAllOrderDetails(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderDetailMapperCustom).selectAllOrderDetailsByOrderId(1);
    }

    @Test
    void selectAllOrderDetails_EmptyList() {
        when(orderDetailMapperCustom.selectAllOrderDetailsByOrderId(999)).thenReturn(List.of());

        List<OrderDetails> result = orderDetailService.selectAllOrderDetails(999);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(orderDetailMapperCustom).selectAllOrderDetailsByOrderId(999);
    }

    @Test
    void selectAllOrderDetails_NullList() {
        when(orderDetailMapperCustom.selectAllOrderDetailsByOrderId(999)).thenReturn(null);

        List<OrderDetails> result = orderDetailService.selectAllOrderDetails(999);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(orderDetailMapperCustom).selectAllOrderDetailsByOrderId(999);
    }
}
