package com.zbkj.admin.service;

import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.vo.ProCategoryCacheVo;

import java.util.List;

public interface CircleProductCategoryService {

    /**
     * 获取圈层端商品分类列表
     * @return 商品分类列表
     */
    List<MerchantProductCategory> getCircleProductCategoryList();

    /**
     * 获取圈层端商品分类缓存树
     * @return 商品分类缓存树
     */
    List<ProCategoryCacheVo> getCacheTree();
}
