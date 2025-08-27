package com.java.TrainningJV.mappers.mapper;


import com.java.TrainningJV.models.Role;

public interface RoleMapper {

   
    int deleteByPrimaryKey(Integer id);

    
    int insert(Role row);

    
    int insertSelective(Role row);

    
    Role selectByPrimaryKey(Integer id);

    
    int updateByPrimaryKeySelective(Role row);

    
    int updateByPrimaryKey(Role row);


}