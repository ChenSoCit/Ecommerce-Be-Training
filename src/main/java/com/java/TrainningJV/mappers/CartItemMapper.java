package com.java.TrainningJV.mappers;

import org.apache.ibatis.annotations.Param;

import com.java.TrainningJV.models.CartItem;

public interface CartItemMapper {
   
    int deleteByPrimaryKey(Integer id);

    // insert cart item
    int insert(CartItem row);

    int insertSelective(CartItem row);

    CartItem selectByCartIdAndProductId(@Param("cartId") Integer cartId,  @Param("productId") Integer productId);
 
    CartItem selectByPrimaryKey(Integer Id);

    int updateByPrimaryKeySelective(CartItem row);

    int updateByPrimaryKey(CartItem row);
}