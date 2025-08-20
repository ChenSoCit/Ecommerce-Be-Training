package com.java.TrainningJV.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddCartItemRequest {
    Integer cartId;
    Integer productId;
    Integer quantity; 
}
