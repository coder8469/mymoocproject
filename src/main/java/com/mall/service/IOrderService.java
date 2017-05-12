package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.vo.OrderVo;

import java.util.Map;

/**
 * Created by Administrator on 2017-5-5.
 */
public interface IOrderService{
    ServerResponse pay(Integer userId, Long orderId, String path);
    ServerResponse alipayCallBack(Map<String,String> params);
    ServerResponse queryOrderPay(Integer userId, Long orderNo);
    ServerResponse createOrder(Integer userId,Integer shippingId);
    ServerResponse cancle( Integer userId,Long orderNo);
    ServerResponse getOrderCart(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
    ServerResponse<PageInfo> getOrderList(int pageNum,int pageSize);
    ServerResponse<OrderVo> orderDetail(Long orderNo);
    ServerResponse<PageInfo> searchOrder(Long orderNo,int pageNum,int pageSize);
    ServerResponse<String> sendGoods(Long orderNo);
}
