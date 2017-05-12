package com.mall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.service.IProductService;
import com.mall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017-5-4.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("product.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProduct(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("productList.do")
    @ResponseBody
    public ServerResponse<PageInfo> productList(@RequestParam(value = "keyword",required = false )String keyword,
                                                @RequestParam(value = "categoryId",required = false )Integer categoryId,
                                                @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                                @RequestParam(value = "orderBy")String orderBy){
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);

    }
}
