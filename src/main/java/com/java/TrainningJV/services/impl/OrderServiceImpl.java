package com.java.TrainningJV.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.TrainningJV.common.DateRangeUtil;
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
import com.java.TrainningJV.services.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final OrderMapperCustom orderMapperCustom;
    private final OrderDetailMapper orderDetailMapper;
    private final CartSevice cartSevices;
    private final CartItemMapperCustom cartItemMapperCustom;
    private final CartMapper cartMapper;

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        log.info("created order order request, {}", orderRequest);
        User exitingUser = userMapper.selectByPrimaryKey(orderRequest.getUserId());
        if (exitingUser == null) {
            throw new ResourceNotFoundException("User","id:", orderRequest.getUserId());
        }

        Order newOrder = Order.builder()
            .userId(orderRequest.getUserId())
            .fullName(orderRequest.getFullName())
            .email(orderRequest.getEmail())
            .phone(orderRequest.getPhone())
            .address(orderRequest.getAddress())
            .status(OrderStatus.pending)
            .orderDate(LocalDateTime.now())
    
        .build();
        int rows = orderMapper.insert(newOrder);
        if (rows != 1) throw new IllegalStateException("Insert failed");

        return newOrder;
    }

    @Override
    public Order findOrderById(Integer id) {
        log.info("find order by order id");
        Order existingOrder = orderMapper.selectByPrimaryKey(id);
        if (existingOrder == null) {
            throw new ResourceNotFoundException("Order", "id:", id);
        }
        return existingOrder;
    }

    @Override
    public Order updateOrder(Integer id, OrderRequest request) {
        log.info("Updating order with id = {}, request = {}", id, request);

        Order existingOrder = orderMapper.selectByPrimaryKey(id);
        if (existingOrder == null) {
            throw new ResourceNotFoundException("Order", "id:", id);
        }

        // Xác định status mới
        OrderStatus newStatus;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                newStatus = OrderStatus.valueOf(request.getStatus().trim());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid status value: " + request.getStatus());
            }
        } else {
            // Giữ nguyên status cũ nếu không gửi
            newStatus = existingOrder.getStatus();
        }

        // Tạo entity mới để update, giữ lại các giá trị không đổi
        Order updateOrder = Order.builder()
                .id(id)  // bắt buộc có id để update
                .userId(existingOrder.getUserId()) // giữ nguyên
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .orderDate(existingOrder.getOrderDate()) // giữ nguyên ngày tạo
                .status(newStatus)
                .build();

        int rows = orderMapper.updateByPrimaryKey(updateOrder);
        if (rows == 0) {
            log.warn("Update failed for order id = {}", id);
            throw new BadRequestException("Update failed for order id = " + id);
        }
        return updateOrder;
    }


    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        // Kiểm tra tồn tại
        Order existing = orderMapper.selectByPrimaryKey(id);
        if (existing == null) {
            log.info("Order with id: {} not found", id);
            throw new ResourceNotFoundException("Order", "id:", id);
        }
        // Xóa các order details liên quan
        orderDetailMapper.deleteByOrderId(id);
       
        // Xóa order
        log.info("Deleting order with id: {}", id);
        int rowsOrder = orderMapper.deleteByPrimaryKey(id);
        if (rowsOrder == 0) {
            log.warn("Delete failed for order id = {}", id);
            throw new BadRequestException("Delete failed for order id = " + id);
        }

    }


    @Override
    public List<Order> getOrders( ) {
        log.info("Get all orders");
        return orderMapperCustom.findAllOrder();
    }

    @Override
    @Transactional
    public List<Order> getOrdersByUserId(Integer userId) {
        log.info("Get orders by user id: {}", userId);
        User existingUser = userMapper.selectByPrimaryKey(userId);
        if (existingUser == null) {
            log.info("User not found with id: {}", userId);
            throw new ResourceNotFoundException("User", "id:", userId);
        }   
        List<Order> orders = orderMapperCustom.findOrderByUserId(userId);
        return orders;
    }

    @Override
    @Transactional
    public Order createOrderFormCart(Integer cartId) {
        Cart cart = cartMapper.selectByPrimaryKey(cartId);
        if (cart == null) throw new ResourceNotFoundException("Cart not found");

        List<CartItem> items = cartItemMapperCustom.findByCartId(cartId);
        if(items.isEmpty()){
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal totalMoney = cartItemMapperCustom.calculateCartTotal(cartId);

        User user = userMapper.selectByPrimaryKey(cart.getUserId());

        Order newOrder = Order.builder()
            .userId(cart.getUserId())
            .fullName(user.getFirstName() + " " +user.getLastName())
            .address(user.getAddress())
            .email(user.getEmail())
            .phone(user.getPhone())
            .orderDate(LocalDateTime.now())
            .status(OrderStatus.pending)
            .totalMoney(totalMoney)
        .build();
        
        orderMapper.insert(newOrder);

        for(CartItem item : items){
            OrderDetails orderDetails = OrderDetails.builder()
                .orderId(newOrder.getId())
                .productId(item.getProductId())
                .numberOfProducts(item.getQuantity())
                .price(item.getPrice())
                .totalMoney(item.getTotal())
            .build();

            orderDetailMapper.insert(orderDetails);
        }
        cartSevices.deleteCart(cartId);
        log.info("Successfully created order {} from cart {}", newOrder.getId(), cartId);
        return newOrder;
    }

    @Override
    public List<Order> findOrderByDay() {
        log.info("Arrange order by total money ASC");
        return orderMapperCustom.findOrderByDay();
    }

    @Override
    public List<Order> findOrderByWeek() {
        log.info("Arrange order Week by total money ASC");
        return orderMapperCustom.findOrderByWeek();
    }

    @Override
    public List<Order> findOrderByMonth() {
        log.info("Arrange order month by total money ASC");
        return orderMapperCustom.findOrderByMonth();
    }


    @Override
    public List<Order> findOrderByYear() {
        log.info("Arrange order year by total money ASC");
        return orderMapperCustom.findOrderByYear();
    }

    @Override
    public List<Order> findOrdersByRange(LocalDate startDate, LocalDate endDate) {
        return orderMapperCustom.findOrderByTypeOrRange( startDate, endDate);
    }

    @Override
    public OrderStatsResponse staticsOrder(Integer userId, StatsRange range, LocalDate from, LocalDate to) {
        
        LocalDate fromDate;
        LocalDate toDate;
        String rangeLabel;

        if(range == StatsRange.CUSTOM || (range == null && from != null && to != null)){
            LocalDate[] dates = DateRangeUtil.getRangeCustom(from, to);
            fromDate = dates[0];
            toDate = dates[1];
            rangeLabel = "custom";
        }else if(range != null){
            LocalDate[] dates = DateRangeUtil.getRange(range);
            fromDate = dates[0];
            toDate = dates[1];
            rangeLabel = range.name().toLowerCase();
        }
        else{
            throw new IllegalArgumentException("Invalid range: both range and from/to are null");
        }

        return orderMapperCustom.statisticalOrder(fromDate, toDate, userId, rangeLabel);
    }
    
}
