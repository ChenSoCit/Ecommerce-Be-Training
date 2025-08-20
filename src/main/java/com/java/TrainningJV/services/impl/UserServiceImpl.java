package com.java.TrainningJV.services.impl;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.dtos.request.UserRoleRequest;
import com.java.TrainningJV.dtos.response.RoleCountResponse;
import com.java.TrainningJV.dtos.response.UserPageResponse;
import com.java.TrainningJV.dtos.response.UserPagingResponse;
import com.java.TrainningJV.dtos.response.UserResponse;
import com.java.TrainningJV.dtos.response.UserWithOrderResponse;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.RoleMapper;
import com.java.TrainningJV.mappers.UserMapper;
import com.java.TrainningJV.mappers.mapperCustom.OrderMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.RoleMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.UserMapperCustom;
import com.java.TrainningJV.models.Order;
import com.java.TrainningJV.models.Role;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleMapperCustom roleMapperCustom;
    private final UserMapperCustom userMapperCustom;
    private final OrderMapperCustom orderMapperCustom;

	@Override
	public User getUser(Integer id) {
		log.info("calling getUser with id: {}", id);
        
        if (id == null || id <= 0) {
            log.warn("Invalid user id: {}", id);
            throw new IllegalArgumentException("Invalid user id: " + id);
        }
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn("User not found with id: {}", id);
            throw new ResourceNotFoundException("User", "id", id);
        }
        log.info("User found: {}", user);
        return user;
	}

    @Override
    public int createUser(UserRequest userRequest) {
        log.info("Creating user with request: {}", userRequest);
        // kiem tra hoac tao role neu chua ton tai

        User users = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .address(userRequest.getAddress())
                .dateOfBirth(Date.valueOf(userRequest.getDateOfBirth()))
                .gender(userRequest.getGender())
                .phone(userRequest.getPhoneNumber())
                .roleId(userRequest.getRoleId())
                .build();

        int result = userMapper.insert(users);
        if (result > 0) {
            log.info("User created successfully: {}", users);
            return users.getId(); // Assuming the ID is set after creation
        } else {
            throw new RuntimeException("Failed to create user");
        }
    }

    @Override
    public int updateUser(Integer id, UserRequest userRequest) {
        log.info("Updating user with id: {} and request: {}", id, userRequest);

        User existingUser = userMapper.selectByPrimaryKey(id);
        if (existingUser == null) {
            log.info("User not found id: {}", id);
            throw new ResourceNotFoundException("User", "id",id);
        }
        User updatedUser = User.builder()
            .id(id)
            .firstName(userRequest.getFirstName())
            .lastName(userRequest.getLastName())
            .email(userRequest.getEmail())
            .password(userRequest.getPassword())
            .address(userRequest.getAddress())
            .phone(userRequest.getPhoneNumber())
            .gender(userRequest.getGender())
            .dateOfBirth(Date.valueOf(userRequest.getDateOfBirth()))
            .roleId(userRequest.getRoleId())  
            .build(); 

        return userMapper.updateByPrimaryKey(updatedUser);
    }

    @Override
    public int deleteUser(Integer id) {
    log.info("Deleting user with id: {}", id);

    User existingUser = userMapper.selectByPrimaryKey(id);
    if (existingUser == null) {
        log.info("User not found id: {}", id);
        throw new ResourceNotFoundException("User", "id",id);
    }

    int deletedRows = userMapper.deleteByPrimaryKey(id);
    if (deletedRows <= 0) {
        throw new RuntimeException("Failed to delete user with id: " + id);
    }

    log.info("User deleted successfully with id: {}", id);
    return deletedRows;
    }

    @Override
    public List<User> getUserNoneRole() {
        log.info("calling getUserNoneRole");
        return userMapperCustom.getUserNoneRole();
    }

    @Override
    public List<User> getUserRole(Integer roleId) {
        log.info("calling getUserRole with roleId: {}", roleId);
        Role existingRole  = roleMapper.selectByPrimaryKey(roleId);
        if(existingRole == null){
            log.info("Role not found id: {}", roleId);
            throw new ResourceNotFoundException("Role" ,":", roleId);
        }
        return userMapperCustom.getUserRole(roleId);
    }

    @Override
    public List<RoleCountResponse> getRoleCount() {
        log.info("calling getRoleCount");
        return userMapperCustom.countUserRole();
    }

    @Override
    public List<UserWithOrderResponse> getAllUsersWithOrders() {
        log.info("calling getAllUsersWithOrders");
        return userMapperCustom.getUsersWithOrders();
        
    }

    @Override
    @Transactional()
    public UserResponse getUserWithOrders(Integer id) {
        log.info("calling getUserWithOrders with id: {}", id);
        User existingUser = userMapper.selectByPrimaryKey(id);
        if(id == null || id <= 0){
            log.info("Invalid user id: {} ", id);
            throw new IllegalArgumentException("Invalid user id: " + id); 
        }

        if (existingUser == null) {
            log.warn("User with id {} not found", id);
            throw new ResourceNotFoundException("User", "id", id);
        }
    
        Role role = roleMapper.selectByPrimaryKey(existingUser.getRoleId());

        List<Order> orders = orderMapperCustom.findOrderByUserId(existingUser.getId());

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(existingUser.getId());
        userResponse.setFirstName(existingUser.getFirstName());
        userResponse.setLastName(existingUser.getLastName());
        userResponse.setEmail(existingUser.getEmail());
        userResponse.setDateOfBirth(existingUser.getDateOfBirth());
        userResponse.setGender(existingUser.getGender());
        userResponse.setAddress(existingUser.getAddress());
        userResponse.setCreatedAt(existingUser.getCreatedAt());
        userResponse.setUpdatedAt(existingUser.getUpdatedAt());
        userResponse.setPhone(existingUser.getPhone());
        userResponse.setPassword(existingUser.getPassword());
        userResponse.setRoleName(role.getName());
        userResponse.setOrders(orders);
        
        return  userResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addUserRole(UserRoleRequest userRoleRequest) { 
    log.info("Adding user role with request: {}", userRoleRequest);
        // Kiem tra role
        Role role = roleMapperCustom.findRoleByName(userRoleRequest.getRoleName());
        if (role == null) {
            log.warn("Role with name {} not found", userRoleRequest.getRoleName());
            role  = new Role();
            role.setId(userRoleRequest.getRoleId());
            role.setName(userRoleRequest.getRoleName());
            roleMapper.insert(role);
        }
        // kiem tra email da ton tai
        User existingUser = userMapperCustom.findByEmail(userRoleRequest.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already exists: " + userRoleRequest.getEmail());
        }
        
        // Tao user moi
        User newUser = User.builder()
        .firstName(userRoleRequest.getFirstName())
        .lastName(userRoleRequest.getLastName())
        .email(userRoleRequest.getEmail())
        .password(userRoleRequest.getPassword())
        .address(userRoleRequest.getAddress())
        .dateOfBirth(Date.valueOf(userRoleRequest.getDateOfBirth()))
        .gender(userRoleRequest.getGender())
        .phone(userRoleRequest.getPhoneNumber())
        .roleId(role.getId())
        .build();
        return userMapper.insert(newUser);
    }

    @Override
    public UserPageResponse getAllUsers(int page, int size) {
        log.info("Fetching all users with pagination: page={}, size={}", page, size);

        int offset = (page - 1) * size;
        List<UserPagingResponse> users = userMapperCustom.getAllUsers(offset, size);

        int totalElements = userMapperCustom.countTotalUsers();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(page);
        userPageResponse.setPageSize(size);
        userPageResponse.setTotalElements(totalElements);
        userPageResponse.setTotalPages(totalPages);
        userPageResponse.setUsers(users);
        log.info("get user successfull");

        return userPageResponse;
    }

    @Override
    public User findUserByPhone(String phone) {
    log.info("Finding user by phone: {}", phone);
        if (phone == null || phone.isEmpty()) {
            log.warn("Phone number is null or empty");
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        User user = userMapperCustom.findByPhone(phone);
        if (user == null) {
            log.warn("User not found with phone: {}", phone);
            throw new ResourceNotFoundException("User", "phone", phone);
        }
        return user;
    }

}
