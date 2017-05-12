package com.mall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.pojo.Category;
import com.mall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017-5-1.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加商品类别
     * @param categoryName
     * @param parentId
     * @return
     */
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加类别信息错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//此分类是可用的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySccessMessage("添加类别成功");
        }
        return ServerResponse.createByErrorMessage("添加类别失败");
    }

    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改类别信息失败");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        //选择性更新
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySccessMessage("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名称失败");
    }

    public ServerResponse<List<Category>> getChildrenParallerCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySccessMessage(categoryList);
    }

    /**
     *  递归查询
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildreById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChidCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        for(Category categoryItem : categorySet){
            categoryIdList.add(categoryItem.getId());
        }
        return ServerResponse.createBySccessMessage(categoryIdList);
    }

    /**
     * 递归算法算出子节点
     * @return
     */
    private Set<Category> findChidCategory(Set<Category> categorySet,Integer categoryId){
        //通过categoryId查询当前Category
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //根据当前categroyId查询子类
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChidCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
