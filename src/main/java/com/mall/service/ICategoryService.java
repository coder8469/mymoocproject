package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2017-5-1.
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);
    ServerResponse setCategoryName(Integer categoryId,String categoryName);
    ServerResponse<List<Category>> getChildrenParallerCategory(Integer categoryId);
    ServerResponse<List<Integer>> selectCategoryAndChildreById(Integer categoryId);
}
