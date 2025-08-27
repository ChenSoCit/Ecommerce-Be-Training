package com.java.TrainningJV.mappers.mapperCustom;

import com.java.TrainningJV.dtos.response.ProductResponse;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapperCustom {
    List<ProductResponse> getAllProducts(@Param("offset") int offset, @Param("size") int size);

    int updateStock (@Param("productId") Integer productId, @Param("qty") int qty);

    int updateIncreaseStock(@Param("productId") Integer productId, @Param("qty") int qty);

    int countProduct();
}
