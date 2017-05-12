package com.mall.controller.portal;

import com.google.common.base.Splitter;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.ICartService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 *  购物车模块
 * Created by Administrator on 2017-5-4.
 */

@Controller
@RequestMapping("/cart/")
public class CartController {

    private static Logger logger = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private ICartService iCartService;

    /**
     *      添加商品到购物车
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping(value = "addcart.do" ,method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse add(HttpSession session,Integer productId,Integer count){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        logger.info("用户ID {},用户名 {}",user.getId(),user.getUsername());
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("updatecart.do")
    @ResponseBody
    public ServerResponse update(HttpSession session,Integer productId,Integer count){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("deletecart.do")
    @ResponseBody
    public ServerResponse deleteByUserId(HttpSession session,String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.ILLEAGE_ARGUMENT.getDesc() );
        }
        return iCartService.deleteProductFromCart(user.getId(),productIds);
    }

    @RequestMapping("getcart.do")
    @ResponseBody
    public ServerResponse cartList(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.getCartList(user.getId());
    }

    /**
     * 购物车全选
     * @param session
     * @return
     */
    @RequestMapping("selectall.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelectChecked(user.getId(),null,Const.CartChecked.CHENKED);
    }

    /**
     * 取消全选
     * @param session
     * @return
     */
    @RequestMapping("unselectall.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelectChecked(user.getId(),null,Const.CartChecked.UNCHECKED);
    }

    /**
     * 购物车单选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("selectsingle.do")
    @ResponseBody
    public ServerResponse selectSingle(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelectChecked(user.getId(),productId,Const.CartChecked.CHENKED);
    }

    /**
     * 购物车取消单选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("unselectsingle.do")
    @ResponseBody
    public ServerResponse unSelectSingle(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelectChecked(user.getId(),productId,Const.CartChecked.UNCHECKED);
    }

    /**
     * 获取购物车中商品总数量
     * @param session
     * @return
     */
    @RequestMapping("getcartcount.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc() );
        }
        return ServerResponse.createBySccessMessage( iCartService.getCartProductCount(user.getId()) );
    }
}
