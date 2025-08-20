package com.java.TrainningJV.mappers.mapperCustom;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.java.TrainningJV.dtos.response.RoleCountResponse;
import com.java.TrainningJV.dtos.response.UserPagingResponse;
import com.java.TrainningJV.dtos.response.UserResponse;
import com.java.TrainningJV.dtos.response.UserWithOrderResponse;
import com.java.TrainningJV.models.User;

public interface UserMapperCustom {

    // find user by email
    User findByEmail(String email);

    // find user by phone
    User findByPhone(String phone);

    // Lấy danh sách người dùng không có role
    List<User> getUserNoneRole();

    // Lấy danh sách người dùng theo roleId
    List<User> getUserRole(@Param("roleId") Integer roleId);

    // Đếm số lượng user theo từng role
    List<RoleCountResponse> countUserRole();

    // Lấy tất cả người dùng kèm theo đơn hàng
    List<UserWithOrderResponse> getUsersWithOrders();

    // Lấy  người dùng  kèm theo đơn hàng theo id
    UserResponse getUserWithOrders(@Param("id") Integer id);

    // Lấy tất cả người dùng với phân trang
    List<UserPagingResponse> getAllUsers(@Param("offset") int page, @Param("size") int size);

    // count user
    int countTotalUsers();
}