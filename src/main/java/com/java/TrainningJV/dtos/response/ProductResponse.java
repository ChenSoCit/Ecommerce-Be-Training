package com.java.TrainningJV.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductResponse {
    @JsonProperty("product_id")
    private Integer id;

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("description")
    private String description;
    
    @JsonProperty("price")
    private BigDecimal price;

    @JsonIgnore
    private Integer stockQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonProperty("category_id")
    private Integer categoryId;
    
}
