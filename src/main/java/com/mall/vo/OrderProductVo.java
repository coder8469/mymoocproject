package com.mall.vo;


import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017-5-6.
 */
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotatlPrice;
    private String imageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotatlPrice() {
        return productTotatlPrice;
    }

    public void setProductTotatlPrice(BigDecimal productTotatlPrice) {
        this.productTotatlPrice = productTotatlPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
