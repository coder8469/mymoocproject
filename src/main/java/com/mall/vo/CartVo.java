package com.mall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017-5-4.
 */
public class CartVo {
    private List<CartProductVo> cartProductVoList; //封装购物车中单品信息
    private BigDecimal cartSumPrice;    //购物车中已勾选的商品总价
    private Boolean allChecked; //是否全部选中
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartSumPrice() {
        return cartSumPrice;
    }

    public void setCartSumPrice(BigDecimal cartSumPrice) {
        this.cartSumPrice = cartSumPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
