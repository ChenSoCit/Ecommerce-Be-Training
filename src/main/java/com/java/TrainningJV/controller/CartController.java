package com.java.TrainningJV.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.java.TrainningJV.dtos.request.AddCartItemRequest;
import com.java.TrainningJV.dtos.request.CartRequest;
import com.java.TrainningJV.dtos.response.ApiResponse;
import com.java.TrainningJV.services.CartSevice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@Slf4j(topic = "CART-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("api/v1/carts")
public class CartController {
    private final CartSevice cartService;
    
    @PostMapping("")
    public ApiResponse createCart(@RequestBody CartRequest request) {
        log.info("Fetching cart for user with ID: {}", request.getUserId());

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cart fetched successfully")
                .data(cartService.createCart(request))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse addItem(@Valid @RequestBody AddCartItemRequest req) {
        log.info("Adding item to cart for user with ID: {}", req.getCartId());
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Item added successfully")
                .data(cartService.addItem(req))
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse getCartItem(@PathVariable int userId){
        log.info("Fetching cart items for user with ID: {}", userId);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cart items fetched successfully")
                .data(cartService.getItems(userId))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse updateCartItem(@PathVariable int id, @RequestBody AddCartItemRequest request){
        log.info("update item");
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("update cart item successfull")
            .data(cartService.updateItem(id, request))
        .build();
    }

   
    @DeleteMapping("/{userid}/items/{productId}")
    public ApiResponse deleteCartItem(@PathVariable int userId, 
                                      @PathVariable int productId)
    {
        cartService.deleteItem(userId, productId);
        return  ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("detele cart item successfull")
                .data("")
            .build();
    }
}
