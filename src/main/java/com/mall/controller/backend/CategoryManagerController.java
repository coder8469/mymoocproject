package com.mall.controller.backend;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017-5-1.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,
                   @RequestParam(value = "parentId" ,defaultValue = "0") int parentId){
        //判断用户是否登录，
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录，请登录");
        }
        //若已登录，则判断是否为管理员身份
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
        return iCategoryService.setCategoryName(categoryId,categoryName);
    }

    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallerCategory(HttpSession session,
                @RequestParam(value = "categoryId" ,defaultValue = "0")Integer categoryId){
        //判断用户是否登录，
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录，请登录");
        }
        //若已登录，则判断是否为管理员身份
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的信息，保持平级不递归
            return iCategoryService.getChildrenParallerCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,
                      @RequestParam(value = "categoryId" ,defaultValue = "0")Integer categoryId){
        //判断用户是否登录，
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录，请登录");
        }
        //若已登录，则判断是否为管理员身份
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的ID和递归子节点的ID
            return iCategoryService.selectCategoryAndChildreById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }
}
