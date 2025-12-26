package com.java.TrainningJV.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.TrainningJV.common.OrderStatus;
import com.java.TrainningJV.common.StatsRange;
import com.java.TrainningJV.dtos.request.OrderRequest;
import com.java.TrainningJV.dtos.response.OrderStatsResponse;
import com.java.TrainningJV.exceptions.BadRequestException;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.CartMapper;
import com.java.TrainningJV.mappers.mapper.OrderDetailMapper;
import com.java.TrainningJV.mappers.mapper.OrderMapper;
import com.java.TrainningJV.mappers.mapper.UserMapper;
import com.java.TrainningJV.mappers.mapperCustom.CartItemMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.OrderMapperCustom;
import com.java.TrainningJV.models.Cart;
import com.java.TrainningJV.models.CartItem;
import com.java.TrainningJV.models.Order;
import com.java.TrainningJV.models.OrderDetails;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.CartSevice;
import com.java.TrainningJV.services.impl.OrderServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "ORDER SERVICE TEST")
@ExtendWith(MockitoExtension.class)
public class OrderSeviceTest {
    @Mock private UserMapper userMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderMapperCustom orderMapperCustom;
    @Mock private OrderDetailMapper orderDetailMapper;
    @Mock private CartSevice cartSevices;
    @Mock private CartItemMapperCustom cartItemMapperCustom;
    @Mock private CartMapper cartMapper;

    @InjectMocks 
    private OrderServiceImpl orderServiceImpl;
    
    private CartSevice cartSevice;
    private Order orderTest;
    private Order orderTest1;
    private User userTest;
    private OrderRequest orderRequest;
    private OrderRequest orderRequest1;
    private Cart cartTest;
    private CartItem cartItemTest1;
    private CartItem cartItemTest2;

    @BeforeEach
    void setUp(){
        userTest = User.builder()
            .id(1)
            .firstName("test")
            .lastName("111")
            .gender("male")
            .email("test111@gmail.com")
            .phone("0987654321")
            .address("address")
            .dateOfBirth(new Date())
            .password("password")
            .roleId(1)
        .build();


        orderTest = Order.builder()
            .id(2)
            .userId(2)
            .fullName("test111")
            .address("address")
            .phone("0987654321")
            .email("test111@gmail.com")
            .orderDate(LocalDateTime.of(2025, 8, 23, 12, 8))
            .status(OrderStatus.pending)
            .totalMoney(BigDecimal.valueOf(100))
        .build();

         orderTest1 = Order.builder()
            .id(1)
            .userId(1)
            .fullName("test111")
            .address("address")
            .phone("0987654321")
            .email("test111@gmail.com")
            .orderDate(LocalDateTime.of(2025, 8, 23, 12, 8))
            .status(OrderStatus.pending)
            .totalMoney(BigDecimal.valueOf(1000))
        .build();

        orderRequest = OrderRequest.builder()
            .userId(1)
            .fullName("test111")
            .address("address")
            .phone("0987654321")
            .email("test111@gmail.com")
        .build();

        orderRequest1 = OrderRequest.builder()
            .userId(999)
            .fullName("test111")
            .address("address")
            .phone("0987654321")
            .email("test111@gmail.com")
        .build();

        cartTest = Cart.builder()
            .id(1)
            .userId(1)
        .build();

        cartItemTest1 = CartItem.builder()
            .id(1)
            .cartId(1)
            .productId(10)
            .quantity(5)
            .price(BigDecimal.valueOf(200))
            .total(BigDecimal.valueOf(1000))
        .build();

        cartItemTest2 = CartItem.builder()
            .id(2)
            .cartId(1)
            .productId(5)
            .quantity(2)
            .price(BigDecimal.valueOf(200))
            .total(BigDecimal.valueOf(400))
        .build();

    }

    @Test
    void createOrder_validRequest_returnOrder(){
        when(userMapper.selectByPrimaryKey(1)).thenReturn(userTest);

        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation ->{
            Order order = invocation.getArgument(0, Order.class);
            order.setId(1);
            return 1;
        });

        Order creatOrder = orderServiceImpl.createOrder(orderRequest);
        assertNotNull(creatOrder);
        assertEquals("test111@gmail.com", creatOrder.getEmail());
        verify(orderMapper, times(1)).insert(any(Order.class));

    }

    @Test
    void createOrder_userNotFound_throwResourceNotFound(){

        when(userMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
        orderServiceImpl.createOrder(orderRequest1));

        assertEquals("User not found with id: 999", ex.getMessage());
        verify(userMapper, times(1)).selectByPrimaryKey(999);
    }

    @Test
    void createOrder_badInsert(){
        when(userMapper.selectByPrimaryKey(1)).thenReturn(userTest);

        when(orderMapper.insert(any(Order.class))).thenReturn(0);

        IllegalStateException ex = assertThrows(IllegalStateException.class,()->
        orderServiceImpl.createOrder(orderRequest));

        assertEquals("Insert failed", ex.getMessage());
        verify(userMapper, times(1)).selectByPrimaryKey(1);
        verify(orderMapper, times(1)).insert(any(Order.class));
    }

    @Test
    void findOrderById_sucsess(){
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);

        Order order = orderServiceImpl.findOrderById(1);
        assertNotNull(order);
        assertEquals("test111", order.getFullName());
        assertEquals(BigDecimal.valueOf(100), order.getTotalMoney());

        verify(orderMapper, times(1)).selectByPrimaryKey(1);
    }

    @Test
    void findOrderById_notFound(){
        when(orderMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,()->
        orderServiceImpl.findOrderById(999));

        assertEquals("Order not found with id: 999", ex.getMessage());

        verify(orderMapper, times(1)).selectByPrimaryKey(999);
    }

    @Test
    void updateOrder_validRequest_returnOrder(){
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);

        when(orderMapper.updateByPrimaryKey(any(Order.class))).thenReturn(1);

        Order orderUpdate = orderServiceImpl.updateOrder(1, orderRequest);
        assertNotNull(orderUpdate);
        assertEquals("0987654321", orderUpdate.getPhone());
        verify(orderMapper, times(1)).selectByPrimaryKey(1);
        verify(orderMapper, times(1)).updateByPrimaryKey(any(Order.class));
    }

    @Test
    void updateOrder_notFound_throwResourcNotFound(){
        when(orderMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()->
        orderServiceImpl.updateOrder(999, orderRequest));

        assertEquals("Order not found with id: 999", ex.getMessage());
        verify(orderMapper, times(1)).selectByPrimaryKey(999);
    }

//    @Test
//    void updateOrder_badUpdate_throwUpdateFail(){
//        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
//
//        when(orderMapper.updateByPrimaryKey(any(Order.class))).thenReturn(0);
//
//        BadRequestException ex = assertThrows(BadRequestException.class,()->
//        orderServiceImpl.updateOrder(1, orderRequest));
//
//        assertEquals("Update failed for order id = ", ex.getMessage());
//        verify(orderMapper, times(1)).selectByPrimaryKey(1);
//        verify(orderMapper, times(1)).updateByPrimaryKey(any(Order.class));
//    }

    @Test
    void deleteOrder_success(){
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);

        when(orderMapper.deleteByPrimaryKey(1)).thenReturn(1);

        orderServiceImpl.deleteOrder(1);

        verify(orderMapper, times(1)).selectByPrimaryKey(1);
        verify(orderMapper, times(1)).deleteByPrimaryKey(1);
    }

    @Test
    void deleteOrder_notFound_throwNotFound(){
        when(orderMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,()->
        orderServiceImpl.deleteOrder(999));

        assertEquals("Order not found with id: 999", ex.getMessage());
        verify(orderMapper, times(1)).selectByPrimaryKey(999);
    }

    @Test
    void getOrders_success(){
        when(orderMapperCustom.findAllOrder()).thenReturn(Arrays.asList(orderTest, orderTest1));

        List<Order> orders = orderServiceImpl.getOrders();

        assertNotNull(orders);
        assertEquals(2, orders.size());

        verify(orderMapperCustom, times(1)).findAllOrder();
    }

    @Test
    void getOrderByUserId_success(){
        when(userMapper.selectByPrimaryKey(1)).thenReturn(userTest);

        when(orderMapperCustom.findOrderByUserId(1)).thenReturn(Arrays.asList(orderTest));

        List<Order> orders = orderServiceImpl.getOrdersByUserId(1);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(BigDecimal.valueOf(100), orders.get(0).getTotalMoney());
    }

    @Test
    void getOrderByUserId_notfound_throwNotFound(){
        when(userMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class
            , () -> orderServiceImpl.getOrdersByUserId(999));
        assertEquals("User not found with id: 999", ex.getMessage());
        verify(userMapper, times(1)).selectByPrimaryKey(999);
    }

    @Test
    void createOrderFormCart_success(){
        when(cartMapper.selectByPrimaryKey(1)).thenReturn(cartTest);
        when(cartItemMapperCustom.findByCartId(1)).thenReturn(Arrays.asList(cartItemTest1, cartItemTest2));
        when(cartItemMapperCustom.calculateCartTotal(1)).thenReturn(BigDecimal.valueOf(1400));
        when(userMapper.selectByPrimaryKey(1)).thenReturn(userTest);
        

        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0, Order.class);
            order.setId(1);
            return 1;
        });

        when(orderDetailMapper.insert(any(OrderDetails.class))).thenReturn(1);

        Order result = orderServiceImpl.createOrderFormCart(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(BigDecimal.valueOf(1400), result.getTotalMoney());

        verify(orderMapper, times(1)).insert(any(Order.class));
        verify(orderDetailMapper, times(2)).insert(any(OrderDetails.class));
        
    }

    @Test
    void createOrderFormCart_notFound_throwNotFound(){
        when(cartMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () ->{
            orderServiceImpl.createOrderFormCart(999);
        });

        assertEquals("Cart not found", ex.getMessage());
        verify(cartMapper, times(1)).selectByPrimaryKey(999);
    }

    @Test
    void staticsOrder_withCustomRange_returnCustom(){
        LocalDate from = LocalDate.of(2025, 8, 01);
        LocalDate to = LocalDate.of(2025,8 , 31);

        OrderStatsResponse mockOrderStatsResponse = new OrderStatsResponse();
        when(orderMapperCustom.statisticalOrder(from, to, 1, "custom"))
        .thenReturn(mockOrderStatsResponse);

        OrderStatsResponse result = orderServiceImpl.staticsOrder(1,StatsRange.CUSTOM, from, to);

        assertNotNull(result);
        assertEquals(mockOrderStatsResponse, result);
        verify(orderMapperCustom, times(1)).statisticalOrder(from, to, 1, "custom");
    }

    @Test
    void staticsOrder_nullRangeCustom_returnCustom(){
        LocalDate from = LocalDate.of(2025, 8, 01);
        LocalDate to = LocalDate.of(2025, 8, 31);

        OrderStatsResponse mockOrderStatsResponse = new OrderStatsResponse();
        when(orderMapperCustom.statisticalOrder(from, to, 1, "custom"))
        .thenReturn(mockOrderStatsResponse);

        OrderStatsResponse result = orderServiceImpl.staticsOrder(1, null, from, to);
        assertNotNull(result);
        assertEquals(mockOrderStatsResponse, result);
        verify(orderMapperCustom, times(1)).statisticalOrder(from, to, 1, "custom");
    }

//    @Test
//    void staticsOrder_withRangeWeek_returnWeek(){
//        LocalDate start = LocalDate.of(2025, 8, 25);
//        LocalDate end = LocalDate.of(2025, 8, 31);
//
//        OrderStatsResponse mockResponse = new OrderStatsResponse();
//        when(orderMapperCustom.statisticalOrder(start, end,2, "week")).thenReturn(mockResponse);
//
//        OrderStatsResponse result = orderServiceImpl.staticsOrder(2, StatsRange.WEEK, null, null);
//
//        assertNotNull(result);
//        assertEquals(mockResponse, result);
//        verify(orderMapperCustom, times(1)).statisticalOrder(start, end, 2, "week");
//
//    }

    @Test
    void staticsOrder_null_throwException(){
        assertThrows(IllegalArgumentException.class, () ->{
            orderServiceImpl.staticsOrder(1, null, null, null);
        });
        verify(orderMapperCustom, never()).statisticalOrder(null, null, 0, null);

    }

    @Test
    void findOrderByDay_success() {
        List<Order> mockOrders = Arrays.asList(orderTest, orderTest1);
        when(orderMapperCustom.findOrderByDay()).thenReturn(mockOrders);

        List<Order> result = orderServiceImpl.findOrderByDay();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderMapperCustom).findOrderByDay();
    }

    @Test
    void findOrderByWeek_success() {
        List<Order> mockOrders = Arrays.asList(orderTest);
        when(orderMapperCustom.findOrderByWeek()).thenReturn(mockOrders);

        List<Order> result = orderServiceImpl.findOrderByWeek();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderMapperCustom).findOrderByWeek();
    }

    @Test
    void findOrderByMonth_success() {
        List<Order> mockOrders = Arrays.asList(orderTest, orderTest1);
        when(orderMapperCustom.findOrderByMonth()).thenReturn(mockOrders);

        List<Order> result = orderServiceImpl.findOrderByMonth();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderMapperCustom).findOrderByMonth();
    }

    @Test
    void findOrderByYear_success() {
        List<Order> mockOrders = Arrays.asList(orderTest1);
        when(orderMapperCustom.findOrderByYear()).thenReturn(mockOrders);

        List<Order> result = orderServiceImpl.findOrderByYear();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderMapperCustom).findOrderByYear();
    }

    @Test
    void findOrdersByRange_success() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<Order> mockOrders = Arrays.asList(orderTest, orderTest1);
        
        when(orderMapperCustom.findOrderByTypeOrRange(startDate, endDate)).thenReturn(mockOrders);

        List<Order> result = orderServiceImpl.findOrdersByRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderMapperCustom).findOrderByTypeOrRange(startDate, endDate);
    }

    @Test
    void createOrderFormCart_emptyCart_throwBadRequest() {
        when(cartMapper.selectByPrimaryKey(1)).thenReturn(cartTest);
        when(cartItemMapperCustom.findByCartId(1)).thenReturn(Arrays.asList());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderServiceImpl.createOrderFormCart(1);
        });

        assertEquals("Cart is empty", ex.getMessage());
        verify(cartMapper).selectByPrimaryKey(1);
        verify(cartItemMapperCustom).findByCartId(1);
    }

    @Test
    void updateOrder_withStatus_success() {
        OrderRequest orderRequestWithStatus = OrderRequest.builder()
            .userId(1)
            .fullName("Updated Name")
            .address("Updated Address")
            .phone("0987654321")
            .email("updated@gmail.com")
            .status("delivered")
        .build();

        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(orderMapper.updateByPrimaryKey(any(Order.class))).thenReturn(1);

        Order result = orderServiceImpl.updateOrder(1, orderRequestWithStatus);

        assertNotNull(result);
        assertEquals(OrderStatus.delivered, result.getStatus());
        verify(orderMapper).selectByPrimaryKey(1);
        verify(orderMapper).updateByPrimaryKey(any(Order.class));
    }

    @Test
    void updateOrder_invalidStatus_throwException() {
        OrderRequest orderRequestInvalidStatus = OrderRequest.builder()
            .userId(1)
            .fullName("Test")
            .address("Address")
            .phone("0987654321")
            .email("test@gmail.com")
            .status("invalid_status")
        .build();

        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderServiceImpl.updateOrder(1, orderRequestInvalidStatus);
        });

        assertEquals("Invalid status value: invalid_status", ex.getMessage());
        verify(orderMapper).selectByPrimaryKey(1);
    }

    @Test
    void deleteOrder_badDelete_throwBadRequest() {
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(orderMapper.deleteByPrimaryKey(1)).thenReturn(0);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderServiceImpl.deleteOrder(1);
        });

        assertEquals("Delete failed for order id = 1", ex.getMessage());
        verify(orderMapper).selectByPrimaryKey(1);
        verify(orderMapper).deleteByPrimaryKey(1);
    }

    @Test
    void updateOrder_badUpdate_throwBadRequest() {
        when(orderMapper.selectByPrimaryKey(1)).thenReturn(orderTest);
        when(orderMapper.updateByPrimaryKey(any(Order.class))).thenReturn(0);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderServiceImpl.updateOrder(1, orderRequest);
        });

        assertEquals("Update failed for order id = 1", ex.getMessage());
        verify(orderMapper).selectByPrimaryKey(1);
        verify(orderMapper).updateByPrimaryKey(any(Order.class));
    }
    
}
