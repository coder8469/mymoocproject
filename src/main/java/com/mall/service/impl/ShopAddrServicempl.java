package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.ShippingMapper;
import com.mall.pojo.Shipping;
import com.mall.service.IShopAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017-5-5.
 */
@Service("iShopAddrService")
public class ShopAddrServicempl implements IShopAddrService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySccessMessage("新增地址成功",result);
        }
        return  ServerResponse.createByErrorMessage("新增地址失败");
    }

    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int rowCount = shippingMapper.deleteByUserIdShippingId(userId,shippingId);
        if(rowCount > 0){
            return ServerResponse.createBySccessMessage("删除地址成功");
        }
        return  ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse<String> update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySccessMessage("修改地址成功");
        }
        return  ServerResponse.createByErrorMessage("修改地址失败");
    }

    public ServerResponse<Shipping> get(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping != null){
            return ServerResponse.createBySccessMessage(shipping);
        }
        return  ServerResponse.createByErrorMessage("查询地址失败");
    }

    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectShippingList(userId,pageNum,pageSize);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySccessMessage(pageInfo);
    }
}
