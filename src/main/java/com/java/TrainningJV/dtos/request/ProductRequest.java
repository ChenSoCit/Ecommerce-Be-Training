package com.java.TrainningJV.dtos.request;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest implements Serializable {
    
    @NotNull(message = "name product not null")
    private String nameProduct;
    @NotNull(message = "description not null")
    private String description;

    @NotNull(message = "price not null")
    @Min(value = 1, message = "price must be not minimine 1")
    private BigDecimal price;

    @NotNull(message = "stock quantity not null")
    @Min(value = 1, message = "stock quantity must be not minimine 1")
    private Integer stockQuantity;

    private Integer categoryId;
}
