package com.java.TrainningJV.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.dtos.response.ProductPageResponse;
import com.java.TrainningJV.dtos.response.ProductResponse;
import com.java.TrainningJV.exceptions.BadRequestException;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.CategoryMapper;
import com.java.TrainningJV.mappers.mapper.ProductMapper;
import com.java.TrainningJV.mappers.mapperCustom.ProductMapperCustom;
import com.java.TrainningJV.models.Category;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.services.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "PRODUCT-SERVICE")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductMapperCustom productMapperCustom;
    private final CategoryMapper categoryMapper;

    @Override
    public ProductPageResponse getAllProducts(int page, int size) {
        log.info("Getting all products with paging: page{} size{}", page, size);
        int offset = (page - 1 ) * size;
        List<ProductResponse> productPage = productMapperCustom.getAllProducts(offset, size);

        int totalElements = productMapperCustom.countProduct();
        int totalPages = (int) Math.ceil((double)totalElements/size);
        ProductPageResponse productPageResponse = new ProductPageResponse();
        productPageResponse.setPageNumber(page);
        productPageResponse.setPageSize(size);
        productPageResponse.setTotalElements(totalElements);
        productPageResponse.setTotalPages(totalPages);
        productPageResponse.setProduct(productPage);

        log.info("get product all successfull");

        return productPageResponse;
    }

    @Override
    public Product getProductById(Integer id) {
        log.info("Get product by id: " + id);
        Product exitingProduct = productMapper.selectByPrimaryKey(id);
        if (exitingProduct == null) {
            log.info("Product with id: " + id + " not found");
            throw new ResourceNotFoundException("Product", "id:", id);
        }

        log.info("Product with id: {} ", id );
        return exitingProduct;
    }

    @Override
    @Transactional
    public Product addProduct(ProductRequest productRequest) {
        log.info("Add product: {} ", productRequest);
        
        Category exitingCategory = categoryMapper.selectByPrimaryKey(productRequest.getCategoryId());
        if(exitingCategory == null){
            log.info("Category with id: {} not found", productRequest.getCategoryId());
            throw new ResourceNotFoundException("Category", "id:", productRequest.getCategoryId());
        }
        
        Product newProduct = Product.builder()
                .name(productRequest.getNameProduct())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .stockQuantity(productRequest.getStockQuantity())
                .categoryId(productRequest.getCategoryId())
                .build();
        int result = productMapper.insert(newProduct);  
        if(result > 0) {
            log.info("Product created successfully: {}", newProduct);
            Product product = productMapper.selectByPrimaryKey(newProduct.getId());
            return product;
        } else {
            throw new BadRequestException("Failed to create product");
        }
    
    }

    @Override
    public void deleteProduct(Integer id) {
        log.info("Delete product: {} ", id);
        Product exitingProduct = productMapper.selectByPrimaryKey(id);

        if(exitingProduct == null){
            log.info("Product not found: {}", id);
            throw new ResourceNotFoundException("Product", "id:", id);
        }
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public Product updateProduct(Integer id, ProductRequest productRequest) {
        log.info("Update product with id: {} and request: {}", id, productRequest);
        Product existingProduct = productMapper.selectByPrimaryKey(id);
        if (existingProduct == null) {
           throw new ResourceNotFoundException("Product", "id:", id);
        }
        
        Product updatedProduct = Product.builder()
                .id(id)
                .name(productRequest.getNameProduct())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .stockQuantity(productRequest.getStockQuantity())
                .build();
        
        log.info("Updated product: {}", updatedProduct);
        int rows = productMapper.updateByPrimaryKey(updatedProduct);
        if(rows != 1) {
            log.error("Failed to update product with id: {}", id);
            throw new BadRequestException("Failed to update product");
        }
        Product productUpadate = productMapper.selectByPrimaryKey(id);
        log.info("Product updated successfully: {}", productUpadate);
        return productUpadate;
    }
}
