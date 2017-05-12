package com.mall.vo;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017-5-4.
 */
public class CartProductVo {
    private Integer cartId;         //购物车ID
    private Integer userId;         //用户ID
    private Integer productId;      //产品ID
    private int quantity;
    private String productName;     //产品名称
    private String productSubTitle;  //产品标题
    private String productMainImage; //产品主图
    private BigDecimal productPrice; //产品价格
    private Integer produdtStatus;   //产品状态
    private BigDecimal productSumPrice; //产品总价
    private Integer productStock;    //产品库存
    private Integer productChecked;  //是否已勾选
    private String limitQuantity;    //限制数量返回结果

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubTitle() {
        return productSubTitle;
    }

    public void setProductSubTitle(String productSubTitle) {
        this.productSubTitle = productSubTitle;
    }

    public String getProductMainImage() {
        return productMainImage;
    }

    public void setProductMainImage(String productMainImage) {
        this.productMainImage = productMainImage;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProdudtStatus() {
        return produdtStatus;
    }

    public void setProdudtStatus(Integer produdtStatus) {
        this.produdtStatus = produdtStatus;
    }

    public BigDecimal getProductSumPrice() {
        return productSumPrice;
    }

    public void setProductSumPrice(BigDecimal productSumPrice) {
        this.productSumPrice = productSumPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductChecked() {
        return productChecked;
    }

    public void setProductChecked(Integer productChecked) {
        this.productChecked = productChecked;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }
}
