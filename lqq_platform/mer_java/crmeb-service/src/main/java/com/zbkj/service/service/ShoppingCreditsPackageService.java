package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;

import java.util.List;

/**
 * @author HZW
 * @description ShoppingCreditsPackageService 接口
 * @date 2025-12-04
 */
public interface ShoppingCreditsPackageService extends IService<ShoppingCreditsPackage> {

    /**
     * 购物金套餐列表
     */
    List<ShoppingCreditsPackage> findList(Integer merId, Integer showStatus);

    ShoppingCreditsPackage getByIdException(Integer id);

    /**
     * 购物金套餐显示状态变更
     * @param id 套餐ID
     */
    Boolean updateShowStatus(Integer id, Integer showStatus);

    /**
     * 购物金套餐列表 - 根据id列表
     */
    List<ShoppingCreditsPackage> findByIdList(List<Integer> idList);
}