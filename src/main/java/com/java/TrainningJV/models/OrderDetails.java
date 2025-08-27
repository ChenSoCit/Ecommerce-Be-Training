package com.java.TrainningJV.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderDetails {
    
    private Integer id;
    
    private Integer orderId;

    private Integer productId;

    private BigDecimal price;

    private Integer numberOfProducts;
    
    private BigDecimal totalMoney;

    private String color;

}