package com.mall.dao;

import com.mall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdShippingId(@Param("userId") Integer userId,
                                 @Param("shippingId") Integer shippingId);

    int updateByShipping(@Param("shipping") Shipping shipping);

    Shipping selectByUserIdShippingId(@Param("userId") Integer userId,
                                      @Param("shippingId") Integer shippingId);

    List<Shipping> selectShippingList(@Param("userId") Integer userId,
                                      @Param(value = "pageNum") Integer pageNum,
                                      @Param(value = "pageSize") Integer pageSize);
}