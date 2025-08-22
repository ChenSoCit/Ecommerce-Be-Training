package com.java.TrainningJV.mappers.mapperCustom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.java.TrainningJV.models.Order;

public interface OrderMapperCustom {

    // Lấy tất cả order với phân trang
    List<Order> findAllOrder();

    // Lấy order theo userId
    List<Order> findOrderByUserId(@Param("userId") Integer userId);

    // So luong order
    int countTotalOrder();

    // Tong tien
    int totalMoneyOrder(@Param("orderId") Integer orderId, @Param("delta") BigDecimal delta);

    List<Order> findOrderByDay();

    List<Order> findOrderByWeek();

    List<Order> findOrderByMonth();

    List<Order> findOrderByYear();

    List<Order> findOrderByTypeOrRange(@Param("startDate") LocalDate startDate
                                        , @Param("endDate") LocalDate endDate);                              
}
