package com.java.TrainningJV.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.dtos.request.UserRoleRequest;
import com.java.TrainningJV.dtos.response.RoleCountResponse;
import com.java.TrainningJV.dtos.response.UserPageResponse;
import com.java.TrainningJV.dtos.response.UserPagingResponse;
import com.java.TrainningJV.dtos.response.UserResponse;
import com.java.TrainningJV.dtos.response.UserWithOrderResponse;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private UserRequest userRequest;
    private UserRoleRequest userRoleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUser = User.builder()
                .id(1)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .phone("0987654321")
                .address("Test Address")
                .dateOfBirth(new Date())
                .gender("male")
                .password("password123")
                .roleId(1)
                .build();

        userRequest = new UserRequest();
        userRequest.setFirstName("Test");
        userRequest.setLastName("User");
        userRequest.setEmail("test@example.com");
        userRequest.setPhoneNumber("0987654321");
        userRequest.setAddress("Test Address");
        userRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userRequest.setGender("male");
        userRequest.setPassword("password123");
        userRequest.setRoleId(1);

        userRoleRequest = new UserRoleRequest();
        userRoleRequest.setFirstName("Test");
        userRoleRequest.setLastName("User");
        userRoleRequest.setEmail("test@example.com");
        userRoleRequest.setPhoneNumber("0987654321");
        userRoleRequest.setAddress("Test Address");
        userRoleRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userRoleRequest.setGender("male");
        userRoleRequest.setPassword("password123");
        userRoleRequest.setRoleId(1);
        userRoleRequest.setRoleName("Admin");
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUser(1)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user sc successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(userService).getUser(1);
    }

    @Test
    void getAllUser_Success() throws Exception {
        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(1);
        userPageResponse.setPageSize(10);
        userPageResponse.setTotalElements(20);
        userPageResponse.setTotalPages(2);
        userPageResponse.setUsers(Arrays.asList(new UserPagingResponse(), new UserPagingResponse()));

        when(userService.getAllUsers(1, 10)).thenReturn(userPageResponse);

        mockMvc.perform(get("/api/users")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user none role"))
                .andExpect(jsonPath("$.data.pageNumber").value(1));

        verify(userService).getAllUsers(1, 10);
    }

    @Test
    void getUserByRole_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.getUserRole(1)).thenReturn(users);

        mockMvc.perform(get("/api/users/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user by role"));

        verify(userService).getUserRole(1);
    }

    @Test
    void getCountRole_Success() throws Exception {
        List<RoleCountResponse> roleCounts = Arrays.asList(new RoleCountResponse(), new RoleCountResponse());
        when(userService.getRoleCount()).thenReturn(roleCounts);

        mockMvc.perform(get("/api/users/role-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get count role"));

        verify(userService).getRoleCount();
    }

    @Test
    void getAllUserWithOrder_Success() throws Exception {
        List<UserWithOrderResponse> usersWithOrders = Arrays.asList(new UserWithOrderResponse());
        when(userService.getAllUsersWithOrders()).thenReturn(usersWithOrders);

        mockMvc.perform(get("/api/users/all-with-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get all users with orders"));

        verify(userService).getAllUsersWithOrders();
    }

    @Test
    void getUserWithOrders_Success() throws Exception {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1);
        when(userService.getUserWithOrders(1)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/users-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User with orders fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(userService).getUserWithOrders(1);
    }

    @Test
    void getUserByPhone_Success() throws Exception {
        when(userService.findUserByPhone("0987654321")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/find-by-phone")
                        .param("phone", "0987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user by phone"));

        verify(userService).findUserByPhone("0987654321");
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data").value(1));

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    void addUserRole_Success() throws Exception {
        when(userService.addUserRole(any(UserRoleRequest.class))).thenReturn(1);

        mockMvc.perform(post("/api/users/add-user-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User role added successfully"))
                .andExpect(jsonPath("$.data").value(1));

        verify(userService).addUserRole(any(UserRoleRequest.class));
    }

    @Test
    void updateUser_Success() throws Exception {
        when(userService.updateUser(anyInt(), any(UserRequest.class))).thenReturn(1);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data").value(1));

        verify(userService).updateUser(anyInt(), any(UserRequest.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        when(userService.deleteUser(1)).thenReturn(1);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService).deleteUser(1);
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        when(userService.deleteUser(999)).thenReturn(0);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService).deleteUser(999);
    }
}
