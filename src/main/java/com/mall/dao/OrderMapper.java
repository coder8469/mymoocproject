package com.mall.dao;

import com.mall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectOrderByOrderId(@Param("userId") Integer userId,@Param("orderNo")  Long orderNo);

    Order selectOrderByOrderNo(@Param("orderNo")  Long orderNo);

    List<Order> getOrderListByUserId(@Param("userId") Integer userId);

    List<Order> getOrderListManager();
}