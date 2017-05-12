package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.vo.ProductDetailVo;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017-5-1.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId, Integer status);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);
    ServerResponse<PageInfo> searchProductByNameAndId(Integer pageNum,Integer pageSize,
                                                      String productName, Integer productId);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    public ServerResponse<PageInfo> getProductByKeywordCategory(
            String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
