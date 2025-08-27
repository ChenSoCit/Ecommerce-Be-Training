package com.java.TrainningJV.services;

import java.time.LocalDate;
import java.util.List;

import com.java.TrainningJV.common.enums.StatsRange;
import com.java.TrainningJV.dtos.request.OrderRequest;
import com.java.TrainningJV.dtos.response.OrderStatsResponse;
import com.java.TrainningJV.models.Order;

public interface  OrderService {

    List<Order> getOrders();

    List<Order> getOrdersByUserId(Integer userId);

    Order createOrder(OrderRequest orderRequest);

    Order createOrderFormCart(Integer cartId);
    
    Order findOrderById(Integer id);

    Order updateOrder(Integer id, OrderRequest orderRequest);

    void deleteOrder(Integer id);

    List<Order> findOrderByDay();

    List<Order> findOrderByWeek();

    List<Order> findOrderByMonth();

    List<Order> findOrderByYear();
    
    List<Order> findOrdersByRange(LocalDate startDate, LocalDate endDate);

    OrderStatsResponse staticsOrder(Integer userId, StatsRange range, LocalDate fromDate, LocalDate toDate);
    
}
