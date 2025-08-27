package com.java.TrainningJV.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.TrainningJV.common.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

    @JsonProperty("order_id")
    private Integer id;

    @JsonProperty("full_name")
    private String fullName;
    
    @JsonIgnore
    private String email;
    
    @JsonProperty("phone")
    private String phone;
   
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("order_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;
    
    @JsonProperty("status")
    private OrderStatus status;
    
    @JsonProperty("total_money")
    private BigDecimal totalMoney;
    
}
