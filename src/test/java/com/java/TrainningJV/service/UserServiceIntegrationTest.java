package com.java.TrainningJV.service;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.java.TrainningJV.mappers.mapper.UserMapper;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.UserService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceIntegrationTest{
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {

        jdbcTemplate.update("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        jdbcTemplate.update("TRUNCATE TABLE roles RESTART IDENTITY CASCADE");

        jdbcTemplate.update("INSERT INTO roles(id, name) VALUES (?, ?)", 2, "ADMIN");

        jdbcTemplate.update("""
            INSERT INTO users (first_name, last_name, email, password, date_of_birth, gender, phone, address, role_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, "Tra", "Nguyen", "tra@gmail.com", "12345667",
            (LocalDate.of(1995, 5, 20)), "Male", "0912345678", "123 HN", 2);
    }
       


    @Test
    void testGetUser_ExistingId_Success() {
        // Given: Insert user vào DB thật qua mapper
        User newUser = User.builder()
                .firstName("Nguyen")
                .lastName("Tra")
                .address("HN")
                .dateOfBirth(new Date())
                .email("tra@gmail.com")
                .gender("male")
                .password("1234567")
                .phone("0987654321")
                .roleId(2)
                .build();

        userMapper.insert(newUser); // ID sẽ được DB tự sinh

        // When: Gọi service
        User result = userService.getUser(newUser.getId());

        // Then: Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("Nguyen", result.getFirstName());
    }

}
