package com.mall.controller.backend;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017-5-1.
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManagerContrller {

    private static final Logger logger = LoggerFactory.getLogger(UserManagerContrller.class);

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse response = iUserService.login(username,password);
        if(response.isSuccess()){
            //如果登录成功，获取用户
            User user = (User) response.getData();
            logger.info("用户身份，role:{}",user.getRole());
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                // 说明登录身份是管理员
                session.setAttribute(Const.CURRENT_USER,user);
                return  response;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }

}
