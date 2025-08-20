package com.java.TrainningJV.mappers.mapperCustom;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.java.TrainningJV.models.CartItem;

public interface CartItemMapperCustom {
    List<CartItem> findByCartId(int cartId);

    CartItem findCartItemByCartId(int cartId);

    CartItem findCartItemByCartIdAndProductId(@Param("cartId") int cartId, @Param("productId") int productId);

    int updateItem(int cartId, int productId, int quantity);
    
    void deleteItem(int cartId, int productId);

    int selectByCartId(int cartId);

    int selectByUserId(int userId);

    void deleteCartItem(@Param("cartId") int cartId, @Param("productId") int productId);

}
