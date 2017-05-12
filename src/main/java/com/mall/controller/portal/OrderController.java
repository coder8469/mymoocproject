package com.mall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017-5-5.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, @RequestParam("orderNo")Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        String path = request.getSession().getServletContext().getRealPath("upload"); //支付二维码保存路径
        logger.info("支付二维码保存路径{}",path);
        return iOrderService.pay(user.getId(),orderNo,path);
    }

    @RequestMapping("alicallback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();//从请求头获取请求数据的集合
        for(Iterator ite = requestParams.keySet().iterator();ite.hasNext();){
            //
            String name = (String)ite.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i=0; i<values.length;i++){
                valueStr = (i == values.length-1) ? valueStr + values[i] : valueStr + values[i] +  ",";
            }
            params.put(name,valueStr);
            logger.info("支付宝回调：sign{} :,trade_status{} : ,参数 : {}",params.get("sign")
                    ,params.get("trade_status"),params.toString());
        }
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey()
            ,"utf-8",Configs.getSignType());
            if( !alipayRSACheckedV2 ){
                return ServerResponse.createByErrorMessage("非法请求，请停止操作");
            }

        } catch (AlipayApiException e) {
            logger.error("支付异常",e);
        }
        ServerResponse serverResponse = iOrderService.alipayCallBack( params );
        if(serverResponse.isSuccess()){
            logger.info("serverResponse.isSuccess() : {}",serverResponse.isSuccess());
            return ServerResponse.createBySccessMessage( Const.AlipayCallBack.RESPONSE_SUCCESS );
        }
        return ServerResponse.createBySccessMessage( Const.AlipayCallBack.RESPONSE_FAILED );
    }

    @RequestMapping("queryorderpay.do")
    @ResponseBody
    public ServerResponse queryOrderPay(HttpSession session, @RequestParam("orderNo")Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        ServerResponse serverResponse =  iOrderService.queryOrderPay(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySccessMessage(true);
        }
        return ServerResponse.createBySccessMessage(false);
    }

    /**
     *  创建订单
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session,@RequestParam("shippingId")Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 未支付订单的取消
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("cancle.do")
    @ResponseBody
    public ServerResponse cancle(HttpSession session,@RequestParam("orderNo")Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iOrderService.cancle(user.getId(),orderNo);
    }

    @RequestMapping("getorderCart.do")
    @ResponseBody
    public ServerResponse getOrderCart(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc() );

        }
        return iOrderService.getOrderCart(user.getId());
    }

    /**
     * 获取订单详细
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,@RequestParam("orderNo") Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    /**
     * 获取订单列表，需要分页
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }
}
