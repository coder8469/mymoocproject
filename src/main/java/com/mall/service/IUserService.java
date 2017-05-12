package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.User;

/**
 * Created by Administrator on 2017-4-30.
 */
public interface IUserService {
    ServerResponse login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> forgetGetQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken);
    ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user);
    ServerResponse<User> updateInfo(User user);
    ServerResponse<User> getInfo(Integer userId);
    ServerResponse checkAdminRole(User user);
}
