package com.java.TrainningJV.mappers;



import com.java.TrainningJV.models.Product;


public interface ProductMapper {
    
    
    int deleteByPrimaryKey(Integer id);

    
    int insert(Product row);

    
    int insertSelective(Product row);


    Product selectByPrimaryKey(Integer id);

   
    int updateByPrimaryKeySelective(Product row);

    
    int updateByPrimaryKey(Product row);
}