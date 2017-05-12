package com.mall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CartMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Cart;
import com.mall.pojo.Product;
import com.mall.service.ICartService;
import com.mall.util.BigdecimalUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.CartProductVo;
import com.mall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017-5-4.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService{

    private static Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加商品到购物车
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGE_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGE_ARGUMENT.getDesc());
        }
        logger.info("userId {}, productId {},count {},",userId,productId,count);
        Cart cart = cartMapper.selectCartByUserIdproductId(userId,productId);
        logger.info("cart{}",cart);
        if(cart == null){
            //购物车中没有该商品，则添加
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);//添加商品数量
            cartItem.setChecked(Const.CartChecked.CHENKED);//新添加到购物车的商品默认是选中状态
            cartItem.setProductId(productId);  //设置产品及用户ID
            cartItem.setUserId(userId);
               cartMapper.insert(cartItem); //插入数据库
        }else {
            //购物车中已有该商品
            cart.setQuantity( cart.getQuantity()+count ); //累加商品数量
            cartMapper.updateByPrimaryKeySelective(cart);//更新购物车
        }
        return ServerResponse.createBySccessMessage( this.getCartVoLimit(userId) );
    }

    /**
     * 更新购物车中商品 （数量）
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGE_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGE_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdproductId(userId, productId);
        if (cart != null) {
            //查询到购物车不为空
            cart.setQuantity( cart.getQuantity()+count );
        }
        cartMapper.updateByPrimaryKeySelective(cart);//更新购物车
        return ServerResponse.createBySccessMessage( this.getCartVoLimit(userId) );
    }

    public ServerResponse<CartVo> deleteProductFromCart(Integer userId,String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            //ids为空，返回错误提示信息
            return ServerResponse.createByErrorCodeMessage( ResponseCode.ILLEAGE_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGE_ARGUMENT.getDesc() );
        }
        cartMapper.deleteProductFromCart(userId,productIdList);//更新购物车
        return ServerResponse.createBySccessMessage( this.getCartVoLimit(userId) );
    }

    public ServerResponse<CartVo> getCartList(Integer userId){
        return ServerResponse.createBySccessMessage( this.getCartVoLimit(userId) );
    }

    public ServerResponse<CartVo> selectOrUnSelectChecked(Integer userId ,Integer productId,int  checked){
        cartMapper.selectOrUnSelectChecked(userId,productId,checked);
        return ServerResponse.createBySccessMessage( this.getCartVoLimit(userId) );
    }

    public int getCartProductCount(Integer userId){
        return cartMapper.getCartProductCount(userId);
    }
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        logger.info("getCartVoLimit userId{}",userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartSumPrice  = new BigDecimal("0");
        //判断购物车是否为空
        if(CollectionUtils.isNotEmpty(cartList)){
            //不为空，遍历。购物车对象中存放的是商品信息条目
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setCartId(cartItem.getId());
                logger.info("getCartVoLimit cartItem.getId(){}",cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                logger.info("getCartVoLimit cartItem.getUserId(){}",cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                logger.info("getCartVoLimit cartItem.getProductId(){}",cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProdudtStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存数量大于购买数量
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.CartProductLimit.LIMIT_SUCCESS);
                    }else {
                        //库存不足，需要修改购物车中可购买的数量
                        buyLimitCount = product.getStock();
                        //设置限购
                        cartProductVo.setLimitQuantity(Const.CartProductLimit.LIMIT_FAIL);
                        //设置购物车中有效购买数量，更新到数据库
                        Cart cartLessCount = new Cart();
                        cartLessCount.setQuantity(buyLimitCount);
                        cartLessCount.setId(cartItem.getId());
                        cartMapper.updateByPrimaryKeySelective(cartLessCount);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductSumPrice(BigdecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if(cartItem.getChecked() == Const.CartChecked.CHENKED){
                    //产品已经勾选
                    cartSumPrice = BigdecimalUtil.add(cartSumPrice.doubleValue(),cartProductVo.getProductSumPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartSumPrice(cartSumPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked( isAllChecked(userId) );
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean isAllChecked(Integer userId){
        if(userId == null ) return false;
        logger.info("isAllChecked userId{}",userId);
        return cartMapper.isAllChecked(userId) == 0;
    }
}
