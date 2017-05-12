package com.mall.service.impl;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.common.TokenCache;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Administrator on 2017-4-30.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            logger.info("用户名不存在");
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //todo 密码MD5
        String pwd = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,pwd);
        if(user == null){
            logger.info("登录密码错误");
            return ServerResponse.createByErrorMessage("密码错误");
        }
        logger.info("用户：{} 登录成功",user.getUsername());
        user.setPassword(StringUtils.EMPTY);
        user.setAnswer(StringUtils.EMPTY);
        return ServerResponse.createBySccessMessage("登录成功",user);
    }

    /**
     *  用户注册，需要校验邮箱和用户名是否唯一
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAEM);
        logger.info("validResponse:{}",validResponse.isSuccess());
        if(!validResponse.isSuccess()){
            //如果用户名已经被注册，返回对应的提示信息
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            //如果邮箱已经被注册，返回对应的提示信息
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOM);       //普通用户
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));    //  密码加密
        int resultCount = userMapper.insert(user);  //插入到数据库
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        logger.info("userMapper.insert(user):{}",resultCount);
        return ServerResponse.createBySccessMessage("注册成功");
    }
    /**
     *  校验用户名或者邮箱是否已经存在
     * @param str 校验信息
     * @param type 校验类型，是邮箱还是用户名
     * @return
     */
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAEM.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                logger.info("userMapper.checkUsername(str): {}",userMapper.checkUsername(str));
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                logger.info("userMapper.checkEmail(str): {}",userMapper.checkEmail(str));
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已经被注册过");
                }
            }
        }else{
            logger.info("参数错误");
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySccessMessage("校验成功");  //邮箱和用户名在数据库中都不存在
    }

    /**
     * 根据用户名找回密码，先判断用户名是否存在，用户名存在时，查找问题是否存在，返回给用户对应信息
     * @param username
     * @return
     */
    public ServerResponse<String> forgetGetQuestion(String username){
        ServerResponse responseValid = this.checkValid(username,Const.USERNAEM);
        logger.info("responseValid : {}",responseValid.isSuccess());
        if(responseValid.isSuccess()){  //用户名在数据库中都不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //校验通过，将问题返回给用户
        String resultQuestion = userMapper.selectQuestionByUsername(username);
        logger.info("resultQuestion : {}",resultQuestion);
        if(StringUtils.isNotBlank(resultQuestion)){
            return ServerResponse.createBySccessMessage(resultQuestion);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空");
    }

    /**
     * 用户输入的用户名已存在的情况下，验证用户名，问题，及问题答案是否一致
     * @param username
     * @param question
     * @param answer
     * @return 如果校验通过，返回给客户端一个Token
     */
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            //说明用户名、问题、答案三者一致，
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username,forgetToken);
            return ServerResponse.createBySccessMessage(forgetToken);
        }
        logger.info("用户名或者答案错误:{}",resultCount);
        return ServerResponse.createByErrorMessage("用户名或者答案错误");
    }

    /**
     * 未登录状态的重置密码，需要判断Token是否为空，所有校验通过，则修改密码成功，
     * @param username
     * @param password
     * @param forgetToken
     * @return
     */
    public ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            //如果Token为空，直接返回错误
            logger.info("token为空");
            return ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAEM);
        if(validResponse.isSuccess()){
            //用户名不存在
            logger.info("用户名不存在{}",validResponse.isSuccess());
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            logger.info("token 无线或过期");
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equalsIgnoreCase(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            logger.info("token 校验结果：{} ，更新密码结果：{}",StringUtils.equalsIgnoreCase(forgetToken,token),rowCount );
            if (rowCount > 0){
                return ServerResponse.createBySccessMessage("密码修改成功");
            }else {
                return ServerResponse.createByErrorMessage("修改密码失败，请重新获取Token信息");
            }
        }
        logger.info("修改密码失败");
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     *  登录状态下的修改密码 在此需要做防止横向越权的操作
     * @param passwordNew
     * @param passwordOld
     * @param user
     * @return
     */
    public ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user){
        //防止横向越权，需要同时校验密码和用户信息
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            logger.info("旧密码错误，resultCount {}",resultCount);
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySccessMessage("密码更新成功");
        }
        logger.info("更新密码 updateCount > 0 表示成功，updateCount {}",updateCount);
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 更新用户个人信息，先查询新的邮箱是否已经存
     * @param user
     * @return
     */
    public ServerResponse<User> updateInfo(User user){
        //username不能更新
        //查询当前要更新的邮箱是否已存在在数据库中
        int rowCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        logger.info("rowCount:{}",rowCount);
        if(rowCount > 0){
            logger.info("邮箱已经存在，需要更换邮箱重试。");
            return ServerResponse.createByErrorMessage("邮箱已经存在，请更换邮箱后尝试重新更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            User userfater = userMapper.selectByPrimaryKey(updateUser.getId());
            return ServerResponse.createBySccessMessage("个人信息更新成功",userfater);
        }
        logger.info("更新跟人信息 updateCount > 0 表示成功，updateCount {}",updateCount);
        return ServerResponse.createByErrorMessage("个人信息修改失败");
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    public ServerResponse<User> getInfo(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        logger.info("username : {}",user.getUsername());
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySccessMessage(user);
    }

    /**
     *  判断是否管理员身份
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySccess();
        }
        logger.info("用户身份检查：{}",user.getRole());
        return ServerResponse.createByError();
    }
}
