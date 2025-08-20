package com.java.TrainningJV.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.dtos.request.UserRoleRequest;
import com.java.TrainningJV.dtos.response.ApiResponse;
import com.java.TrainningJV.dtos.response.UserResponse;
import com.java.TrainningJV.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j(topic = "UserController")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @GetMapping("/{id}")
    public ApiResponse getUserById(@PathVariable Integer id) {
        log.info("Fetching user details: {}", id);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get user sc successfully")
                .data(userService.getUser(id)) 
                .build();
    }


    @GetMapping("")
    public ApiResponse getAllUser(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        log.info("get all user page {} size {}", page, size);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get user none role")
                .data(userService.getAllUsers(page, size))
                .build();
    }

    @GetMapping("/roles/{roleId}")
    public ApiResponse getUserByRole(@PathVariable Integer roleId){
        log.info("Fetching user by role: {}", roleId);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get user by role")
                .data(userService.getUserRole(roleId))
                .build();
    }

    @GetMapping("/role-count")
    public ApiResponse getCountRole(){
        log.info("Fetching count role");
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get count role")
                .data(userService.getRoleCount())
                .build();
    }

    @GetMapping("/all-with-orders")
    public ApiResponse getAllUserWithOrder(){
        log.info("Fetching all users with orders");
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get all users with orders")
                .data(userService.getAllUsersWithOrders())
                .build();
    }

    @GetMapping("/users-orders/{id}")
    public ApiResponse getUserWithOrders(@PathVariable Integer id) {
        log.info("Fetching user with orders for id: {}", id);
        UserResponse userWithOrders = userService.getUserWithOrders(id);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("User with orders fetched successfully")
            .data(userWithOrders)
            .build();
    }

    @GetMapping("/find-by-phone")
    public ApiResponse getUserByPhone(@RequestParam String phone) {
        log.info("Fetching user by phone: {}", phone);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("get user by phone")
                .data(userService.findUserByPhone(phone))
                .build();
    }

    @PostMapping("")
    public ApiResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Creating user: {}", userRequest);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("User created successfully")
                .data(userService.createUser(userRequest))
                .build();
    }

    @PostMapping("/add-user-role")
    public ApiResponse addUserRole(@Valid @RequestBody UserRoleRequest userRoleRequest) {
        log.info("Adding user role: {}", userRoleRequest);

        return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("User role added successfully")
                    .data(userService.addUserRole(userRoleRequest))
                    .build();
    }


    @PutMapping("/{id}")
    public ApiResponse updateUser(@PathVariable int id, @Valid @RequestBody UserRequest userRequest) {
        log.info("Updating user with id: {} and request: {}", id, userRequest);
    
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("User updated successfully")
            .data(userService.updateUser(id, userRequest))
            .build();

    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(@PathVariable int id) {
        log.info("Deleting user with id: {}", id);
        int result = userService.deleteUser(id);
        if (result > 0) {
            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("User deleted successfully")
                    .data(null)
                    .build();
        } else {
            return ApiResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .data(null)
                    .build();
        }
    }
}
