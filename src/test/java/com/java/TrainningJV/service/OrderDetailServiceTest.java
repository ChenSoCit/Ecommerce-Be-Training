package com.java.TrainningJV.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.TrainningJV.dtos.request.OrderDetailRequest;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.OrderDetailMapper;
import com.java.TrainningJV.mappers.OrderMapper;
import com.java.TrainningJV.mappers.ProductMapper;
import com.java.TrainningJV.mappers.mapperCustom.OrderMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.ProductMapperCustom;
import com.java.TrainningJV.models.Order;
import com.java.TrainningJV.models.OrderDetails;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.models.enums.OrderStatus;
import com.java.TrainningJV.services.impl.OrderDetailServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {
    @Mock private OrderDetailMapper orderDetailMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderMapperCustom orderMapperCustom;
    @Mock private ProductMapper productMapper;
    @Mock private ProductMapperCustom productMapperCustom;

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    private Order orderTest;
    private Product productTest;
    private OrderDetailRequest orderDetailResquestTest;
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
            .address("123 Test St")
            .totalMoney(BigDecimal.valueOf(200.0))
            .orderDate(new Date())
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

        orderDetailResquestTest = OrderDetailRequest.builder()
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
    

    // @Test
    // void testGetOrderDetailById_validId_returnOrderDetail() {
    //     when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
    //     when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
       
    //     OrderDetails orderDetails = orderDetailService.selectOrderDetailsByOrderId(1);

    //     assertNotNull(orderDetails);
    //     assertEquals(1, orderDetails.getOrderId());
    //     assertEquals("Red", orderDetails.getColor());
    
    // }


    @Test
    void testCreateOrderDetail_notFound_returnResourceNotFound(){
        when(orderDetailMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            orderDetailService.selectOrderDetailsByOrderId(999);
        });
        assertEquals("Order not found with id: 999", ex.getMessage());
        verify(orderDetailMapper).selectByPrimaryKey(999);
        verifyNoMoreInteractions(orderDetailMapper);
    }

    @Test
    void insertOrderDetails_validData_returnOrderDetails(){

        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
        when(orderDetailMapper.insert(any(OrderDetails.class))).thenReturn(1);
        when(productMapperCustom.updateIncreaseStock(10, 2)).thenReturn(1);
        // when(orderMapperCustom.totalMoneyOrder(1, any(BigDecimal.class))).thenReturn(1);
        
        OrderDetails result = orderDetailService.insertOrderDetails(orderDetailResquestTest);

        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals(1, result.getProductId());
        assertEquals(BigDecimal.valueOf(200), result.getTotalMoney());

        verify(orderMapper).selectByPrimaryKey(1);
        verify(orderDetailMapper).insert(any(OrderDetails.class));
        verify(productMapperCustom).updateStock(10, 2);
        verify(orderMapperCustom).totalMoneyOrder(eq(1), eq(BigDecimal.valueOf(200)));
    }

    
}
