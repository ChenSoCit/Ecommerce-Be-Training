package com.java.TrainningJV.controller;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
public class UserControllerIntegrationTest  {
    @Autowired MockMvc mockMvc;

    @Autowired JdbcTemplate jdbcTemplate;

    String emailToCreate = "tra1+" + System.currentTimeMillis() + "@gmail.com";

    @BeforeEach
    void setup() {

        jdbcTemplate.update("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        jdbcTemplate.update("TRUNCATE TABLE roles RESTART IDENTITY CASCADE");

        jdbcTemplate.update("INSERT INTO roles(id, name) VALUES (?, ?)", 3, "USER");

        jdbcTemplate.update("""
            INSERT INTO users (first_name, last_name, email, password, date_of_birth, gender, phone, address, role_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, "Tra", "Nguyen", "tra@gmail.com", "12345667",
            Date.valueOf(LocalDate.of(1995, 5, 20)), "Male", "0912345678", "123 HN", 3);
}


    @AfterEach
    void cleanup() {
        jdbcTemplate.update("DELETE FROM users WHERE email IN (?, ?)", "tra@gmail.com", emailToCreate);
        // nếu có các dữ liệu khác được tạo trong test, xoá theo điều kiện tương tự
    }

    @Test
    void testCreateUser_ThenCheckDb() throws Exception {
        String json = """
        {
            "firstName": "Tra",
            "lastName": "Nguyen2",
            "email": "%s",
            "password": "12345667",
            "dateOfBirth": "1995-05-20",
            "gender": "Male",
            "phoneNumber": "0912345678",
            "address": "123 Đường ABC, Quận Ha Dong, TP.HN",
            "roleId": 3
        }
        """.formatted(emailToCreate);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
            

        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email = ?",
            Integer.class, emailToCreate
            
        );

        assertEquals(1, count); // DB phải có đúng 1 user được insert
    }

    @Test
    void testCreateUser_InvalidEmailFormat() throws Exception {
    String json = """
        {
            "firstName": "Tra",
            "lastName": "Nguyen",
            "email": "invalid-email",
            "password": "12345667",
            "dateOfBirth": "1995-05-20",
            "gender": "Male",
            "phoneNumber": "0912345678",
            "address": "123 Đường ABC, Quận Ha Dong, TP.HN",
            "roleId": 3
        }
    """;

    mockMvc.perform(MockMvcRequestBuilders
            .post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("email: Invalid email format"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
    String json = """
        {
            "firstName": "Tra Updated",
            "lastName": "Nguyen",
            "email": "tra_updated@gmail.com",
            "password": "12345667",
            "dateOfBirth": "1995-05-20",
            "gender": "Male",
            "phoneNumber": "0999999999",
            "address": "456 HN",
            "roleId": 3
        }
    """;

    mockMvc.perform(MockMvcRequestBuilders  
            .put("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User updated successfully"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));

    String email = jdbcTemplate.queryForObject("SELECT email FROM users WHERE id = ?", String.class, 1);
    assertEquals("tra_updated@gmail.com", email);
    }

    @Test
    void testDeleteUser_Success() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
        .delete("/api/users/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User deleted successfully"));

    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Integer.class, 1);
    assertEquals(0, count);
    }
    
}
