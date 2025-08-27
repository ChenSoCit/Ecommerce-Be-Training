package com.java.TrainningJV.service;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.RoleMapper;
import com.java.TrainningJV.mappers.mapper.UserMapper;
import com.java.TrainningJV.mappers.mapperCustom.OrderMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.RoleMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.UserMapperCustom;
import com.java.TrainningJV.models.Role;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.impl.UserServiceImpl;

import lombok.extern.slf4j.Slf4j;


@Slf4j(topic="SERVICE-TEST")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private  RoleMapper roleMapper;
    @Mock
    private  RoleMapperCustom roleMapperCustom;
    @Mock
    private UserMapperCustom userMapperCustom;
    @Mock
    private OrderMapperCustom orderMapperCustom;

    
    private UserServiceImpl userService;

    private static User test1;
    private static User test2;
    private static UserRequest userRequest;
    private static User existingUser;

    @BeforeAll
    static void beforeAll(){
        test1 = new User();
        test1.setId(1);
        test1.setFirstName("test1");
        test1.setLastName("ok");
        test1.setEmail("test1@gmail.com");
        test1.setDateOfBirth(new Date());
        test1.setGender("male");
        test1.setAddress("HN");
        test1.setPassword("tset1");
        test1.setPhone("0987654321");
        test1.setRoleId(1);
        

        Role  role = new Role();
        role.setId(1);
        role.setName("Admin");
       
    

        test2 = new User();
        test2.setId(2);
        test2.setFirstName("test2");
        test2.setLastName("ok");
        test2.setEmail("test2@gmail.com");
        test2.setDateOfBirth(new Date());
        test2.setGender("male");
        test2.setAddress("HN");
        test2.setPassword("tets2");
        test2.setPhone("0987654321");
        test2.setRoleId(2);
        

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("User");
      

        
        userRequest = new UserRequest();
        userRequest.setFirstName("tset1");
        userRequest.setLastName("ok");
        userRequest.setEmail(  "userRequest@gmail.com");
        userRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userRequest.setGender("male");
        userRequest.setAddress("HN");
        userRequest.setPassword("tets2");
        userRequest.setPhoneNumber("0987654321");
        userRequest.setRoleId(2);

        existingUser = User.builder()
                .id(1)
                .firstName("Old")
                .lastName("User")
                .email("old@example.com")
                .build();
    
    }

    @BeforeEach
    void setUp() {
        // khoi tao buoc trien khai
        userService = new UserServiceImpl(userMapper, roleMapper, roleMapperCustom, userMapperCustom, orderMapperCustom);
    }

    @Test
    void testGetById_success(){
        when(userMapper.selectByPrimaryKey(1)).thenReturn(test1);

        User userResult = userService.getUser(1);
        assertNotNull(userResult);
        assertEquals(1, userResult.getId());
        
    }

    @Test
    void testGetUser_Invalid(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(-1);
        });

        assertEquals("Invalid user id: -1", exception.getMessage());     
    }

    @Test
    void testGetUser_NotFound(){
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->{
            userService.getUser(999);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
    }

    @Test
    void createUser_successfulInsert_shouldReturnId() {
        // Mock: giả lập DB gán ID sau insert
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100); // Giả lập DB tự tăng ID
            return 1; // giả lập insert thành công
        });

        int resultId = userService.createUser(userRequest);

        assertEquals(100, resultId);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void createUser_insertFails_shouldThrowException() {
        when(userMapper.insert(any(User.class))).thenReturn(0); // giả lập insert thất bại

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.createUser(userRequest);
        });

        assertEquals("Failed to create user", ex.getMessage());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void updateUser_Success(){
        when(userMapper.selectByPrimaryKey(1)).thenReturn(existingUser);
        when(userMapper.updateByPrimaryKey(any(User.class))).thenReturn(1);

        int result = userService.updateUser(1, userRequest);
        assertEquals(1, result);
        verify(userMapper, times(1)).updateByPrimaryKey(any(User.class));
    }

    @Test
    void upDateUser_NotFound(){
        when(userMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->{
            userService.updateUser(999, userRequest);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userMapper).selectByPrimaryKey(999);
        verify(userMapper, never()).updateByPrimaryKey(any(User.class));
    }


    @Test
    void deleteUser_Success(){
        when(userMapper.selectByPrimaryKey(1)).thenReturn(existingUser);
        when(userMapper.deleteByPrimaryKey(1)).thenReturn(1);

        int result = userService.deleteUser(1);
        assertEquals(1, result);
        verify(userMapper).selectByPrimaryKey(1);
        verify(userMapper, times(1)).deleteByPrimaryKey(1);
    }
    
    @Test
    void deleteUser_NotFound(){
        when(userMapper.selectByPrimaryKey(999)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999);
        });
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userMapper).selectByPrimaryKey(999);
        verify(userMapper, never()).deleteByPrimaryKey(999);
    }

}
