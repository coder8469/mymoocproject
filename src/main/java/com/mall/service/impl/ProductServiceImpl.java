package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Category;
import com.mall.pojo.Product;
import com.mall.service.ICategoryService;
import com.mall.service.IProductService;
import com.mall.util.DateTimeUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.ProductDetailVo;
import com.mall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-5-1.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            if(org.apache.commons.lang3.StringUtils.isNoneBlank(product.getSubImages())){
                String[] subImageArr = product.getSubImages().split(",");
                if(subImageArr.length > 0){
                    product.setMainImage(subImageArr[0]);
                }
            }
            int rowCount = 0;
            if(product.getId() != null){
                rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0){
                    return ServerResponse.createBySccessMessage("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            }else{
                rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createBySccessMessage("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数错误");
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGE_ARGUMENT.getCode()
                    ,ResponseCode.ILLEAGE_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKey(product);
        if(rowCount > 0){
            return ServerResponse.createBySccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    /**
     *  根据产品ID获取产品详情信息 在这里做了VO(value-object)封装
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGE_ARGUMENT.getCode()
                    ,ResponseCode.ILLEAGE_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySccessMessage(productDetailVo);
    }

    /**
     * 此方法中 封装了图片的保存地址，做了时间格式的转换，封装了其他product的属性
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setImageHost(PropertiesUtil.getProperty(
                "ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //在此使用pagehelper来完成分页的操作
        //1、startpage--start
        PageHelper.startPage(pageNum,pageSize);
        //2、填充SQL 获取查询结果集合
        List<Product> productList = productMapper.getProductList(pageNum,pageSize);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            //遍历集合，对ProductListVo对象进行VO填充
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //3、pagehelper收尾
        PageInfo resultPage = new PageInfo(productList);
        resultPage.setList(productListVoList);
        return ServerResponse.createBySccessMessage(resultPage);
    }
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty(
                "ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProductByNameAndId(Integer pageNum,Integer pageSize,
                             String productName, Integer productId){
        //在此使用pagehelper来完成分页的操作
        //1、startpage--start
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotEmpty(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectProductByNameAndId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            //遍历集合，对ProductListVo对象进行VO填充
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //3、pagehelper收尾
        PageInfo resultPage = new PageInfo(productList);
        resultPage.setList(productListVoList);
        return ServerResponse.createBySccessMessage(resultPage);
    }

    /**
     * 前台商品信息展示，根据ID查询，商品是否在售状态
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGE_ARGUMENT.getCode()
                    ,ResponseCode.ILLEAGE_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySccessMessage(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(
            String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) &&categoryId==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGE_ARGUMENT.getCode(),
                    ResponseCode.ILLEAGE_ARGUMENT.getDesc());
        }
        List<Integer> categoryList = new ArrayList<>();//存放分类产品
        if(categoryId != null){
            //根据ID查询类别
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类，也没有关键字，返回一个空，不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList  = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySccessMessage( pageInfo );
            }
                categoryList = iCategoryService.selectCategoryAndChildreById(category.getId()).getData();
            }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrberBy.PRICE_DESC_ASC.contains(orderBy)){
                String [] arrOrderBy = orderBy.split("_");
                PageHelper.orderBy(arrOrderBy[0]+" "+arrOrderBy[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(
            StringUtils.isBlank(keyword)?null:keyword,categoryList.size()==0?null:categoryList
        );
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList){
        ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySccessMessage(pageInfo);
    }
}
