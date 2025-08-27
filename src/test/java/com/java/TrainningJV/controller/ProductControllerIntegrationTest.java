package com.java.TrainningJV.controller;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        // Dọn bảng và reset identity để kết quả deterministic
        jdbcTemplate.execute("TRUNCATE TABLE products RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE categories RESTART IDENTITY CASCADE");

        // Seed category (id = 1)
        jdbcTemplate.update("INSERT INTO categories (name) VALUES (?)", "Electronics");

        // Seed sẵn 1 product (id = 1)
        jdbcTemplate.update("""
            INSERT INTO products (name, description, price, stock_quantity, created_at, updated_at, category_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, "iPhone 15", "Latest model", 999.99, 50, LocalDateTime.now(), LocalDateTime.now(), 1);
    }

    // ============== Helpers dùng JdbcTemplate ==============
    private Integer countProductsByName(String name) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM products WHERE name = ?",
            Integer.class, name
        );
    }

    private String findProductNameById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT name FROM products WHERE id = ?",
                String.class, id
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Double findProductPriceByName(String name) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT price FROM products WHERE name = ?",
                Double.class, name
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // ========================= TESTS =========================

    @Test
    void createProduct_Success() throws Exception {
        String json = """
            {
            "nameProduct": "iPhone 16",
            "description": "Flagship model",
            "price": 1299.99,
            "stockQuantity": 100,
            "categoryId": 1
            }
        """;

        // 1) Gọi API
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created product successfully"));
            
    // Verify DB
    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM products WHERE name = ?", Integer.class, "iPhone 16");
    assertEquals(1, count);

    Double price = jdbcTemplate.queryForObject(
        "SELECT price FROM products WHERE name = ?", Double.class, "iPhone 16");
    assertEquals(1299.99, price);
    }


    @Test
    void getProduct_ById_ShouldReturn200_WithData() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("iPhone 15"));
        // (Không cần check DB ở test GET này vì đã seed; nhưng có thể check nếu muốn)
    }

    @Test
    void getProduct_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            // chỉnh message cho khớp với ExceptionHandler của bạn
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    void updateProduct_ShouldReturn200_AndPersistChange() throws Exception {
        String updateJson = """
        {
            "nameProduct": "iPhone 15 Pro",
            "description": "Updated model",
            "price": 1099.50,
            "stockQuantity": 40,
            "categoryId": 1
        }
        """;

        mockMvc.perform(put("/api/v1/products/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(MockMvcResultMatchers.status().isOk());
    

        // Check DB sau update
        assertEquals("iPhone 15 Pro", findProductNameById(1));
        assertEquals(1, countProductsByName("iPhone 15 Pro"));
    }

    @Test
    void deleteProduct_ShouldReturn204_AndRemove() throws Exception {
        // Xóa product id=1
        mockMvc.perform(delete("/api/v1/products/{id}", 1))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // DB không còn bản ghi id=1
        assertNull(findProductNameById(1));
    }
}
