package com.java.TrainningJV.models;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Cart {
    
    private Integer id;

    private Integer userId;

    private Date createdAt;

    private Date updatedAt;

}