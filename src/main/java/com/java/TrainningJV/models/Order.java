package com.java.TrainningJV.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.java.TrainningJV.common.enums.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class Order {
    
    private Integer id;
    
    private Integer userId;

    private String fullName;
    
    private String email;
    
    private String phone;
   
    private String address;
    
    private LocalDateTime orderDate;
    
    private OrderStatus status;
    
    private BigDecimal totalMoney;

}