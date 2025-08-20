package com.java.TrainningJV.mappers;

import com.java.TrainningJV.models.Cart;

public interface CartMapper {

    int deleteByPrimaryKey(Integer userId);

    // insert cart
    int insert(Cart row);

    int insertSelective(Cart row);

    // select cart by Id
    Cart selectByPrimaryKey(Integer id);

    Cart selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(Cart row);

    int updateByPrimaryKey(Cart row);
}