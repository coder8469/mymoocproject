package com.mall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/** 常量类
 * Created by Administrator on 2017-4-30.
 */
public class Const {
    public static final String CURRENT_USER = "current_user";
    public interface Role{
        int ROLE_CUSTOM = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员用户
    }
    public static final  String EMAIL = "email";
    public static final  String USERNAEM = "username";

    /**
     *  在查询商品时选择的排序方式，按价格升序、降序等
     */
    public interface ProductListOrberBy{
        Set<String> PRICE_DESC_ASC = Sets.newHashSet("price_desc","price_asc");
    }

    /**
     * 购物车中商品是否选中
     */
    public interface CartChecked{
        //选中
        int CHENKED = 1;
        //未选中
        int UNCHECKED = 0;
    }

    /**
     * 购买商品大于库存后需要限购，返回对应提示
     */
    public interface CartProductLimit{
        //库存有余
        String LIMIT_SUCCESS = "LIMIT_SUCCESS";
        //库存不足
        String LIMIT_FAIL = "LIMIT_FAIL";
    }
    /**
     * 商品是否在售的枚举类
     */
    public enum ProductStatusEnum{
        ON_SALE(1,"在售");

        private int code;
        private String value;

        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     *  订单状态
     */
    public enum OrderStatus{
        CANCLE(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"已关闭"),
        ORDER_CLOSE(60,"已关闭");
        int code;
        String value;

        OrderStatus(int code,String value){
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
        public static OrderStatus valueOfCode(int code){
            for( OrderStatus orderStatus: values() ){
                if( orderStatus.getCode() == code ) return orderStatus;
            }
            throw  new RuntimeException("没有对应的信息");
        }
    }

    /**
     * 支付宝回调结果
     */
    public interface AlipayCallBack{
        String TRADE_STATTUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY "; //订单状态：等待买家付款
        String TRADE_STATTUS_TRADE_SUCCESS = "TRADE_SUCCESS";    //订单状态：买家付款成功
        String RESPONSE_SUCCESS = "success";                     //回调给支付宝的状态信息：交易成功，支付完成
        String RESPONSE_FAILED = "failed";                       //回调给支付宝的状态信息：交易失败，支付完成
    }

    /**
     * 支付平台类型选择，当前仅有支付宝
     */
    public enum PayPlatForm{
        ALIPAY(1,"支付宝")
        ;
        int code;
        String value;
        PayPlatForm(int code,String value){
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
    }
    /**
     *  支付方式，例如支付宝可以有多种支付方式（当面付、APP支付）
     */
    public enum PayType{
        ALIPAYONLINE(1,"支付宝在线支付");
        int code;
        String value;

        PayType(int code,String value){
            this.code = code;
            this.value = value;
        }
        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }

        public static PayType valueOfCode(int code){
            for( PayType payType: values() ){
                if( payType.getCode() == code ) return payType;
            }
            throw  new RuntimeException("没有对应的信息");
        }
    }
}
