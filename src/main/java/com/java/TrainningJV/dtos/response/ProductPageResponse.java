package com.java.TrainningJV.dtos.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPageResponse extends PageResponseAbstract {
    private  List<ProductResponse> product;
}
