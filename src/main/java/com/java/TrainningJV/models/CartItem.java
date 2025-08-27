package com.java.TrainningJV.models;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class CartItem {

    private Integer id;

    private Integer cartId;

    private Integer productId;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal total;

}