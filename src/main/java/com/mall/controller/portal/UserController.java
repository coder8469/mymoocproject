package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
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

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017-4-30.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录的方法
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody   //使用SpringMVC的Jackson插件自动将返回值序列化为json对象
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout( HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        session.removeAttribute(Const.CURRENT_USER);
        logger.info("用户：{}退出登录",user.getUsername());
        return ServerResponse.createBySccess();
    }

    /**
     *  用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     *  校验用户名或者邮箱是否已经存在
     * @param str 信息
     * @param type 校验类型，是邮箱还是用户名
     * @return
     */
    @RequestMapping(value = "checkvalid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取当前登录用户的信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getuserinfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySccessMessage(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
    }

    /**
     * 用户忘记密码时，根据问题找回密码，需要将问题返回给用户。先根据用户输入的用户名查看是否已存在（已注册）
     * @param username
     * @return
     */
    @RequestMapping(value = "forgetquestion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetQuestion(username);
    }

    /**
     * 用户输入的用户名已存在的情况下，验证用户名，问题，及问题答案是否一致
     * @param username  ： 需要找回面的用户名
     * @param question  ： 对应的问题
     * @param answer    ： 对应的答案
     * @return
     */
    @RequestMapping(value = "checkanswer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     *  用户选择了找回密码后，重置密码，需要校验用户名，新密码，Token信息
     * @param username
     * @param password
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "resetpassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken){
        return iUserService.forgetResetPassword(username,password,forgetToken);
    }

    /**
     *  用户在登陆状态下的重置密码，需要先判断登录信息
     * @param passwordNew 新密码
     * @param passwordOld 旧密码
     * @param session
     * @return
     */
    @RequestMapping(value = "onlineresetpassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordNew,String passwordOld,HttpSession session){
        //防止横向越权，需要同时校验密码和用户信息
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordNew,passwordOld,user);
    }

    /**
     * 用户更新个人信息，需要先判断登录状态
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "updateinfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        logger.info("currentUser:{}",currentUser);
        if(currentUser == null){
            return  ServerResponse.createByErrorMessage("请先登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername()); //
        logger.info("currentUser.getUsername():{}",currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInfo(user);
        if(response.isSuccess()){
            logger.info("response.isSuccess():{}",response.isSuccess());
            logger.info("(User) session.getAttribute(Const.CURRENT_USER):{}",(User) session.getAttribute(Const.CURRENT_USER));
            session.removeAttribute(Const.CURRENT_USER);
            //信息更新成功需要将新的User存入到session
            session.setAttribute(Const.CURRENT_USER,response.getData());
            logger.info("(User) session.getAttribute(Const.CURRENT_USER):{}",(User) session.getAttribute(Const.CURRENT_USER));
        }
        logger.info("response:{}",response.getData());
        return response;
    }

    /**
     *  用户在修改个人信息前先获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getinfodetail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfo(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要先登录");
        }
        return iUserService.getInfo(currentUser.getId());
    }
}
