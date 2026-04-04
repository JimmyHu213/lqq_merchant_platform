package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.admin.service.CircleProductCategoryService;
import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.service.service.MerchantProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CircleProductCategoryServiceImpl implements CircleProductCategoryService {

    @Autowired
    private MerchantProductCategoryService categoryService;
    @Autowired
    private CircleHomeService circleHomeService; 

    /**
     * 获取圈层端商品分类列表
     * @return 商品分类列表
     */
    @Override
    public List<MerchantProductCategory> getCircleProductCategoryList() {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new ArrayList<MerchantProductCategory>();
        }
        return categoryService.getCircleProductCategoryList(merIdList);
    }

    /**
     * 获取圈层端商品分类缓存树
     * @return 商品分类缓存树
     */
    @Override
    public List<ProCategoryCacheVo> getCacheTree() {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new ArrayList<ProCategoryCacheVo>();
        }
        return categoryService.getCircleCacheTree(merIdList);
    }
}
