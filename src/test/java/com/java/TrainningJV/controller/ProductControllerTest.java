package com.java.TrainningJV.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.TrainningJV.dtos.request.ProductRequest;
import com.java.TrainningJV.dtos.response.ProductPageResponse;
import com.java.TrainningJV.dtos.response.ProductResponse;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.services.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Product testProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testProduct = Product.builder()
                .id(1)
                .name("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .description("Test Description")
                .stockQuantity(10)
                .categoryId(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRequest = ProductRequest.builder()
                .nameProduct("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .description("Test Description")
                .stockQuantity(10)
                .categoryId(1)
                .build();
    }

    @Test
    void getAllProducts_Success() throws Exception {
        ProductPageResponse productPageResponse = new ProductPageResponse();
        productPageResponse.setPageNumber(0);
        productPageResponse.setPageSize(20);
        productPageResponse.setTotalElements(50);
        productPageResponse.setTotalPages(3);
        productPageResponse.setProduct(Arrays.asList(new ProductResponse(), new ProductResponse()));

        when(productService.getAllProducts(0, 20)).thenReturn(productPageResponse);

        mockMvc.perform(get("/api/v1/products/list")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Get all products"))
                .andExpect(jsonPath("$.data.pageNumber").value(0));

        verify(productService).getAllProducts(0, 20);
    }

    @Test
    void getAllProducts_WithDefaultParams() throws Exception {
        ProductPageResponse productPageResponse = new ProductPageResponse();
        productPageResponse.setPageNumber(0);
        productPageResponse.setPageSize(20);

        when(productService.getAllProducts(0, 20)).thenReturn(productPageResponse);

        mockMvc.perform(get("/api/v1/products/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Get all products"));

        verify(productService).getAllProducts(0, 20);
    }

    @Test
    void getProductById_Success() throws Exception {
        when(productService.getProductById(1)).thenReturn(testProduct);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get product successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Product"));

        verify(productService).getProductById(1);
    }

    @Test
    void createProduct_Success() throws Exception {
        when(productService.addProduct(any(ProductRequest.class))).thenReturn(testProduct);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Created product successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(productService).addProduct(any(ProductRequest.class));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Deleted product successfully"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(productService).deleteProduct(1);
    }

    @Test
    void updateProduct_Success() throws Exception {
        when(productService.updateProduct(anyInt(), any(ProductRequest.class))).thenReturn(testProduct);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Updated product successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Product"));

        verify(productService).updateProduct(anyInt(), any(ProductRequest.class));
    }
}
