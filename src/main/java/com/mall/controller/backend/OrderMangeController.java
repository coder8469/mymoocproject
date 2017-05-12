package com.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import com.mall.service.IProductService;
import com.mall.service.IUserService;
import com.mall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017-5-7.
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderMangeController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("orderlist.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //增加产品的业务逻辑
            return iOrderService.getOrderList(pageNum, pageSize);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping("orderdetail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.orderDetail(orderNo);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }


    @RequestMapping("searchorder.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchOrder(HttpSession session,@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.searchOrder(orderNo,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping("sendgoods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.sendGoods(orderNo);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }
}
