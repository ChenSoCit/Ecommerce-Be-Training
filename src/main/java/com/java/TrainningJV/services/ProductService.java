package com.java.TrainningJV.services;


import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.dtos.response.ProductPageResponse;
import com.java.TrainningJV.models.Product;

public interface ProductService {
    ProductPageResponse getAllProducts(int page, int size);

    Product getProductById(Integer id);

    Product addProduct(ProductRequest productRequest);

    void deleteProduct(Integer id);

    Product updateProduct(Integer id, ProductRequest productRequest);

}
