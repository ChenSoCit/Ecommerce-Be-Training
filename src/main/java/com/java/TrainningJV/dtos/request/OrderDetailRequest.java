package com.java.TrainningJV.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDetailRequest {
    @NotNull(message = "Order id must be not null")
    private Integer orderId;

    @NotNull(message = "product must be not null")
    private Integer productId;

    @NotNull(message = "number of product must be not null")
    @Min(value = 1)
    private Integer numberOfProducts;

    private String color;
}
