package com.java.TrainningJV.services;

import java.util.List;

import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.dtos.request.UserRoleRequest;
import com.java.TrainningJV.dtos.response.RoleCountResponse;
import com.java.TrainningJV.dtos.response.UserPageResponse;
import com.java.TrainningJV.dtos.response.UserResponse;
import com.java.TrainningJV.dtos.response.UserWithOrderResponse;
import com.java.TrainningJV.models.User;

public interface  UserService {
    
    User getUser(Integer id);

    int createUser(UserRequest userRequest);

    int updateUser(Integer id, UserRequest userRequest);

    int deleteUser(Integer id);

    User findUserByPhone(String phone);

    List<User> getUserNoneRole();

    List<User> getUserRole(Integer roleId);

    List<RoleCountResponse> getRoleCount();

    // list all users with their orders
    List<UserWithOrderResponse> getAllUsersWithOrders();

    // get user with orders by id
    UserResponse getUserWithOrders(Integer id);

    UserPageResponse getAllUsers(int page, int size);

    int addUserRole(UserRoleRequest userRoleRequest);
}
