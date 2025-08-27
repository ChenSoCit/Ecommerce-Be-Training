package com.java.TrainningJV.mappers.mapper;

import com.java.TrainningJV.models.Category;

public interface CategoryMapper {
    
    int deleteByPrimaryKey(Integer id);

    
    int insert(Category row);

   
    int insertSelective(Category row);

    
    Category selectByPrimaryKey(Integer id);


    int updateByPrimaryKeySelective(Category row);

    
    int updateByPrimaryKey(Category row);
}