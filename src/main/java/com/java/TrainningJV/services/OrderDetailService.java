package com.java.TrainningJV.services;

import java.util.List;

import com.java.TrainningJV.dtos.request.OrderDetailRequest;
import com.java.TrainningJV.models.OrderDetails;

public interface OrderDetailService {
    OrderDetails insertOrderDetails(OrderDetailRequest request);

    OrderDetails selectOrderDetailsByOrderId(int id);

    List<OrderDetails> selectAllOrderDetails(int orderId);

    List<OrderDetails> selectAllOrderDetails();

    OrderDetails updateOrderDetails(Integer id, OrderDetailRequest request);

    void deleteByOrderId(int OrderId);
    
    void deleteById(int id);
}
