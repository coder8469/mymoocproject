package com.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.pojo.User;
import com.mall.service.IFileService;
import com.mall.service.IProductService;
import com.mall.service.IUserService;
import com.mall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2017-5-1.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    private static Logger logger = LoggerFactory.getLogger(ProductManageController.class);
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    /**
     *
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save_product.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    /**
     * 获取产品详情
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("get_product_detail.do")
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session,Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(HttpSession session,
                           @RequestParam(value = "pageNum" ,defaultValue = "1") Integer pageNum,
                           @RequestParam(value = "pageSize" ,defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping("search_product_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchProductList(HttpSession session,
             @RequestParam(value = "pageNum" ,defaultValue = "1") Integer pageNum,
                 @RequestParam(value = "pageSize" ,defaultValue = "10")Integer pageSize,
                    @RequestParam(value = "productName" ) String productName,
                        @RequestParam(value = "productId" )Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProductByNameAndId(pageNum,pageSize,productName,productId);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                    @RequestParam(value = "upload_file",required = false) MultipartFile file,
                    HttpServletRequest request){
        logger.info("upload");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            logger.info("path:" +path);
            String targetFileNmae = iFileService.upload(file,path);
            logger.info(targetFileNmae);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileNmae;
            logger.info("url:"+url);
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileNmae);
            fileMap.put("url",url);
            return ServerResponse.createBySccessMessage(fileMap);
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，无法进行操作。");
        }
    }

    @RequestMapping(value = "richTextImgUpload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map richTextImgUpload(HttpSession session,
                                 @RequestParam(value = "upload_file",required = false) MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        Map resultMap = Maps.newHashMap();
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员。");
            return resultMap;
        }
        //富文本中按照simditor的要求进行返回
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileNmae = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileNmae)){
                resultMap.put("success",false);
                resultMap.put("msg","文件上传失败。");
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileNmae;
            resultMap.put("success",true);
            resultMap.put("msg","文件上传成功。");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","x-File-Name");
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","您没有管理员权限。");
            return resultMap;
        }
    }
}
