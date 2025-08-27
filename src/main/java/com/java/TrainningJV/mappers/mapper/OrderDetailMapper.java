package com.java.TrainningJV.mappers.mapper;

import com.java.TrainningJV.models.OrderDetails;



public interface OrderDetailMapper {

    int deleteByPrimaryKey(Integer id);

    int deleteByOrderId(Integer orderId);

    int insert(OrderDetails row);

    int insertSelective(OrderDetails row);

    OrderDetails selectByPrimaryKey(Integer id);

    OrderDetails selectByOrderId(Integer id);
   
    int updateByPrimaryKeySelective(OrderDetails row);

    int updateByPrimaryKey(OrderDetails row);
}