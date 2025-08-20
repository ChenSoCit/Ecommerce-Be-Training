package com.java.TrainningJV.services;

import java.util.List;

import com.java.TrainningJV.dtos.request.AddCartItemRequest;
import com.java.TrainningJV.dtos.request.CartRequest;
import com.java.TrainningJV.models.Cart;
import com.java.TrainningJV.models.CartItem;

public interface CartSevice {
    Cart createCart(CartRequest request);

    Cart getCart(int userId);

    List<Cart> getAllCarts();

    List<CartItem> getItems(int userId);

    CartItem addItem(AddCartItemRequest req);

    CartItem updateItem(int id, AddCartItemRequest req);

    void deleteItem(Integer userId, Integer productId);

    
} 