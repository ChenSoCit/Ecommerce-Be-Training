package com.java.TrainningJV.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.dtos.response.ApiResponse;
import com.java.TrainningJV.services.ProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "PRODUCT-CONTROLLER")
@Validated
@RequestMapping("api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public ApiResponse getAllProducts() {
        log.info("Get all products");
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get all products")
                .data(productService.getAllProducts())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse getProductById(@Valid @PathVariable int id) {
        log.info("Get product by id: {}", id);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get product successfully")
                .data(productService.getProductById(id))
                .build();
    }

    @PostMapping("")
    public ApiResponse createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Create product: {}", productRequest);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Created product successfully")
                .data(productService.addProduct(productRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteProduct(@PathVariable int id) {
        log.info("Delete product: {}", id);
        productService.deleteProduct(id);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deleted product successfully")
                .data(null)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse updateProduct(@Valid @PathVariable int id, @Valid @RequestBody ProductRequest productRequest) {
        log.info("Update product with id: {} and request: {}", id, productRequest);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("Updated product successfully")
            .data(productService.updateProduct(id, productRequest))
            .build();
    }
}
