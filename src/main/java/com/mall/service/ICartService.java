package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.vo.CartVo;

/**
 * Created by Administrator on 2017-5-4.
 */
public interface ICartService {
    ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count);
    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);
    ServerResponse<CartVo> deleteProductFromCart(Integer userId,String productIds);
    ServerResponse<CartVo> getCartList(Integer userId);
    ServerResponse<CartVo> selectOrUnSelectChecked(Integer userId ,Integer productId,int  checked);
    int getCartProductCount(Integer userId);
}
