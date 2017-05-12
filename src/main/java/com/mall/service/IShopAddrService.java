package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.pojo.Shipping;

/**
 * Created by Administrator on 2017-5-5.
 */
public interface IShopAddrService {
    ServerResponse add(Integer userId, Shipping shipping);
    ServerResponse<String> del(Integer userId, Integer shippingId);
    ServerResponse<String> update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> get(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}
