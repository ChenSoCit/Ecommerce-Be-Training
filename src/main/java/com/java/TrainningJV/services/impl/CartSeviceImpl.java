package com.java.TrainningJV.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.TrainningJV.dtos.request.AddCartItemRequest;
import com.java.TrainningJV.dtos.request.CartRequest;
import com.java.TrainningJV.exceptions.BadRequestException;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.CartItemMapper;
import com.java.TrainningJV.mappers.mapper.CartMapper;
import com.java.TrainningJV.mappers.mapper.ProductMapper;
import com.java.TrainningJV.mappers.mapperCustom.CartItemMapperCustom;
import com.java.TrainningJV.models.Cart;
import com.java.TrainningJV.models.CartItem;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.services.CartSevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic= "CART SERVICE")
@RequiredArgsConstructor
public class CartSeviceImpl implements CartSevice {
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemMapperCustom cartItemMapperCustom;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Cart createCart(CartRequest request) {
        log.info("Creating cart for user with ID: {}", request.getUserId());
        Cart cart = Cart.builder()
                .userId(request.getUserId())
                .build();
        int result = cartMapper.insert(cart);
        if(result != 1){
            log.error("Failed to create cart for user with ID: {}", request.getUserId());
    
        }
        Cart newCart = cartMapper.selectByUserId(request.getUserId());
        return newCart;
    }


    @Override
    @Transactional(readOnly = true)
    public Cart getCart(int userId) {
        log.info("Fetching cart for user with ID: {}", userId);
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            log.error("Cart not found for user with ID: {}", userId);
            throw new ResourceNotFoundException("Cart","id:", userId);
        }
        return cart;
    }

    @Override
    public List<Cart> getAllCarts() {
        log.info("Fetching all carts");

        
        throw new UnsupportedOperationException("Unimplemented method 'getAllCarts'");
    }

    @Override
    public List<CartItem> getItems(int userId) {
        log.info("Fetching items for user with ID: {}", userId);
        Cart extingCart = cartMapper.selectByUserId(userId);
        if (extingCart == null) {
            log.error("Cart not found for user with ID: {}", userId);
            throw new ResourceNotFoundException("Cart", "userId:", userId);
        }
        List<CartItem> items = cartItemMapperCustom.findByCartId(extingCart.getId());
        if (items.isEmpty()) {
            log.info("No items found for user with ID: {}", userId);
            return List.of(); 
        }
        return items;
    }

    @Override
    @Transactional
    public CartItem addItem(AddCartItemRequest req) {
        log.info("Adding item to cart for user with ID: {}", req.getCartId());
        Cart cart = cartMapper.selectByPrimaryKey(req.getCartId());
        if (cart == null) {
            log.error("Cart not found for user with ID: {}",  req.getCartId());
            throw new ResourceNotFoundException("Cart", "userId:", req.getCartId());
        }
        
        Product product = productMapper.selectByPrimaryKey(req.getProductId());
        if(product.getStockQuantity() < req.getQuantity()) {
            log.error("Product with ID: {} is out of stock", req.getProductId());
            throw new IllegalArgumentException("Product is out of stock");
        }

        CartItem exitingItem = cartItemMapperCustom.findCartItemByCartIdAndProductId( req.getCartId(), req.getProductId());
        if( exitingItem != null){
            exitingItem.setQuantity(exitingItem.getQuantity() + req.getQuantity());
            exitingItem.setTotal(product.getPrice().multiply(BigDecimal.valueOf(exitingItem.getQuantity())));
            cartItemMapper.updateByPrimaryKeySelective(exitingItem);

            return exitingItem;
        }

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        CartItem newCartItem = CartItem.builder()
                .cartId(cart.getId())
                .productId(req.getProductId())
                .quantity(req.getQuantity())
                .price(product.getPrice())
                .total(totalPrice)
                .build();

        cartItemMapper.insert(newCartItem);

        return newCartItem;
    }

    @Override
    @Transactional
    public CartItem updateItem(int id, AddCartItemRequest req) {
        log.info("update item with id: ", id);
        CartItem oldItems = cartItemMapper.selectByPrimaryKey(id);
        if(oldItems == null){
            throw new ResourceNotFoundException("Cart_item", "id:", id);
        }

        CartItem updateCartItem = CartItem.builder()
            .id(id)
            .cartId(oldItems.getCartId())
            .productId(req.getProductId())
            .quantity(req.getQuantity())
            .price(oldItems.getPrice())
            .total(oldItems.getPrice().multiply(BigDecimal.valueOf(req.getQuantity())))
        .build();

        int rows = cartItemMapper.updateByPrimaryKey(updateCartItem);
        if(rows != 1){
            throw new BadRequestException("update cart item fail ");
        }
        return cartItemMapper.selectByPrimaryKey(id);

    }

    @Override
    @Transactional
    public void deleteItem(Integer userId, Integer productId){
        Cart cart = cartMapper.selectByUserId(userId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "User id:", userId);
        }

        CartItem  cartItem = cartItemMapper.selectByCartIdAndProductId(cart.getId(), productId);
         if (cartItem == null) {
        throw new ResourceNotFoundException("Product ", "id:", productId);
        }

        int rows = cartItemMapperCustom.deleteCartItem(cart.getId(), productId);
        if(rows != 1){
            log.error("delete product fail");
            throw new BadRequestException("Delete product found");
        }
        
        log.info("Xóa sản phẩm khỏi giỏ hàng thành công - userId: {}, productId: {}", userId, productId);
    }

    @Override
    @Transactional
    public void deleteCart(Integer cartId) {
        Cart extingCart = cartMapper.selectByPrimaryKey(cartId);
        if(extingCart == null){
            throw new ResourceNotFoundException("cart", "id:", cartId);
        }
        cartItemMapper.deleteByPrimaryKey(cartId);
        log.info("Delete cart with id: ", cartId);

        int rows = cartMapper.deleteByPrimaryKey(cartId);
        if(rows != 1){
            throw new BadRequestException("Delete cart found with id: " + cartId);
        }
    }
     
}
