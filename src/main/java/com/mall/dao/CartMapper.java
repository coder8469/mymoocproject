package com.mall.dao;

import com.mall.pojo.Cart;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdproductId(@Param("userId")Integer userId,
                                     @Param("productId")Integer productId);

    List<Cart> selectCartByUserId(@Param("userId")Integer userId);

    int isAllChecked(Integer userId);

    int deleteProductFromCart(@Param("userId") Integer userId,@Param("productIdList")  List<String> productIdList);

    int selectOrUnSelectChecked(@Param("userId")Integer userId,
                  @Param("productId")Integer productId,@Param("checked")Integer checked);
    int getCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}