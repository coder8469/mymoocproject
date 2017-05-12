package com.mall.dao;

import com.mall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> getProductList(@Param("pageNum")int pageNum, @Param("pageSize")int pageSize);

    List<Product> selectProductByNameAndId(String productName,Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,
                            @Param("categoryIdList")List<Integer> categoryIdList);
}