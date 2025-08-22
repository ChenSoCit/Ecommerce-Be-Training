package com.java.TrainningJV.mappers;

import com.java.TrainningJV.models.Order;

public interface OrderMapper {
    
    int deleteByPrimaryKey(Integer id);

    int insert(Order row);
   
    int insertSelective(Order row);
    
    Order selectByPrimaryKey(Integer id);
    
    int updateByPrimaryKeySelective(Order row);

    int updateByPrimaryKey(Order row);
}