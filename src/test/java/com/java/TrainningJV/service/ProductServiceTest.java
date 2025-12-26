package com.java.TrainningJV.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.dtos.response.ProductPageResponse;
import com.java.TrainningJV.dtos.response.ProductResponse;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.CategoryMapper;
import com.java.TrainningJV.mappers.mapper.ProductMapper;
import com.java.TrainningJV.mappers.mapperCustom.ProductMapperCustom;
import com.java.TrainningJV.models.Category;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.services.impl.ProductServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "PRODUCT-SERVICE-TEST")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock private ProductMapper productMapper;
    @Mock private ProductMapperCustom productMapperCustom;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private  Product productTest;
    private  Category categoryTest;
    private  ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productTest =  Product.builder()
                .id(1)  
                .name( "Test Product 1")
                .price(BigDecimal.valueOf(100.0))
                .description("Description for Test Product 1")
                .stockQuantity(10)
                .categoryId(1)
                .createdAt(LocalDateTime.of(2025, 8, 23, 12, 8))
                .updatedAt(LocalDateTime.of(2025, 8, 23, 12, 8))
                .build();

        categoryTest = Category.builder()
                .id(1)
                .name("Test Category")
                .build();

        productRequest = ProductRequest.builder()
                .nameProduct("Test Product 1")
                .price(BigDecimal.valueOf(100.0))
                .description("Description for Test Product 1")
                .stockQuantity(10)
                .categoryId(1)
                .build();
        
        }


    @Test
    void testGetProductById_success() {
        
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
        
        Product product = productService.getProductById(1);
        
        assertNotNull(product);
        assertEquals("Test Product 1", product.getName());
        assertEquals(BigDecimal.valueOf(100.0), product.getPrice());
    }

    @Test
    void testGetProductById_notFound() {
        when(productMapper.selectByPrimaryKey(99)).thenReturn(null);
        
        ResourceNotFoundException exception = 
            assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99));
        
        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productMapper).selectByPrimaryKey(99);
        verifyNoMoreInteractions(productMapper);
    }

    @Test
    void createProduct_validRequest_returnProduct(){
        when(categoryMapper.selectByPrimaryKey(1)).thenReturn(categoryTest);

        when(productMapper.insert(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1); // Giả lập DB tự tăng ID
            return 1; // giả lập insert thành công
        });

        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);

        Product createdProduct = productService.addProduct(productRequest);
        assertNotNull(createdProduct);
        assertEquals("Test Product 1", createdProduct.getName());
        verify(productMapper, times(1)).insert(any(Product.class));

    }

    @Test
   void createProduct_categoryNotFound_throwResourceNotFound(){

        productRequest = ProductRequest.builder()
                .nameProduct("Test Product 1")
                .price(BigDecimal.valueOf(100.0))
                .description("Description for Test Product 1")
                .stockQuantity(10)
                .categoryId(999)
                .build();
        

        when(categoryMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.addProduct(productRequest);
        });
       
        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryMapper, times(1)).selectByPrimaryKey(999);

    }

    @Test
    void createProduct_insertFails_throwBadRequestException() {
        when(categoryMapper.selectByPrimaryKey(1)).thenReturn(categoryTest);

        when(productMapper.insert(any(Product.class))).thenReturn(0); // giả lập insert thất bại

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(productRequest);
        });

        assertEquals("Failed to create product", exception.getMessage());
        verify(productMapper, times(1)).insert(any(Product.class));
    }

    @Test
    void updateProduct_validRequest_returnUpdatedProduct(){
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);
        
        when(productMapper.updateByPrimaryKey(any(Product.class))).thenReturn(1);

        Product updatedProduct = productService.updateProduct(1, productRequest);
        assertNotNull(updatedProduct);
        assertEquals("Test Product 1", updatedProduct.getName());
        verify(productMapper, times(1)).updateByPrimaryKey(any(Product.class));
    }

    @Test
    void updateProduct_productNotFound_throwResourceNotFound(){

        when(productMapper.selectByPrimaryKey(999)).thenReturn(null);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(999, productRequest);
        });
        assertEquals("Product not found with id: 999", ex.getMessage());
        verify(productMapper, times(1)).selectByPrimaryKey(999);
    }

    

    @Test
    void deleteProduct_success() {
        when(productMapper.selectByPrimaryKey(1)).thenReturn(productTest);

        when(productMapper.deleteByPrimaryKey(1)).thenReturn(1);

        productService.deleteProduct(1);
        verify(productMapper, times(1)).deleteByPrimaryKey(1);
    }

    @Test
    void deleteProduct_notFound() {
        when(productMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999);
        });

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productMapper).selectByPrimaryKey(999);
        verify(productMapper, times(0)).deleteByPrimaryKey(any(Integer.class));
   }

    @Test
    void getAllProducts_Success() {
        List<ProductResponse> mockProducts = Arrays.asList(
            new ProductResponse(),
            new ProductResponse()
        );
        
        when(productMapperCustom.getAllProducts(0, 20)).thenReturn(mockProducts);
        when(productMapperCustom.countProduct()).thenReturn(50);

        ProductPageResponse result = productService.getAllProducts(1, 20);

        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertEquals(50, result.getTotalElements());
        assertEquals(3, result.getTotalPages()); // Math.ceil(50/20) = 3
        assertEquals(2, result.getProduct().size());
        
        verify(productMapperCustom).getAllProducts(0, 20);
        verify(productMapperCustom).countProduct();
    }

    @Test
    void getAllProducts_PageTwo() {
        List<ProductResponse> mockProducts = Arrays.asList(
            new ProductResponse()
        );
        
        when(productMapperCustom.getAllProducts(10, 10)).thenReturn(mockProducts);
        when(productMapperCustom.countProduct()).thenReturn(15);

        ProductPageResponse result = productService.getAllProducts(2, 10);

        assertNotNull(result);
        assertEquals(2, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getProduct().size());
        
        verify(productMapperCustom).getAllProducts(10, 10);
        verify(productMapperCustom).countProduct();
    }


}
