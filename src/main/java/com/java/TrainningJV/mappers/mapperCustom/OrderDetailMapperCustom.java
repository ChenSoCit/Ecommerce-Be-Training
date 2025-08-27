package com.java.TrainningJV.mappers.mapperCustom;

import java.util.List;

import com.java.TrainningJV.models.OrderDetails;

public interface OrderDetailMapperCustom {
    List<OrderDetails> selectAllOrderDetails();

    List<OrderDetails> selectAllOrderDetailsByOrderId(int orderId);
} 
