package com.java.TrainningJV.mappers.mapper;




import com.java.TrainningJV.models.User;

public interface UserMapper {
   
    // delete user by id
    int deleteByPrimaryKey(Integer id);

    // create a new user
    int insert(User row);
    
    int insertSelective(User row);

    // select user detail by id
    User selectByPrimaryKey(Integer id);
    
    int updateByPrimaryKeySelective(User row);

    // update user detail by id
    int updateByPrimaryKey(User row);
}